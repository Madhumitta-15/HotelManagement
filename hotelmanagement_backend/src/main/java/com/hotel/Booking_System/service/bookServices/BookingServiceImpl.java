package com.hotel.Booking_System.service.bookServices;

import com.hotel.Booking_System.dto.bookingdto.BookingDetailsDTO;
import com.hotel.Booking_System.dto.bookingdto.BookingRequestDTO;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.RoomUnavailableException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.*;
import com.hotel.Booking_System.model.enums.BookingStatus;
import com.hotel.Booking_System.model.enums.PaymentStatus;
import com.hotel.Booking_System.repository.bookRepo.BookingRepository;
import com.hotel.Booking_System.repository.hotelRepo.RoomRepository;
import com.hotel.Booking_System.repository.loyaltyRepo.LoyaltyRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.loyaltyService.LoyaltyService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional; // <-- Import Optional

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final  BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final GuestRepository guestRepo;
    private final PaymentService paymentService;
    private final LoyaltyService loyaltyService;
    private final LoyaltyRepository loyaltyRepository;
    private final JWTServiceImpl jwtService;

    @Override
    @Transactional
    public Booking bookRoom(BookingRequestDTO request, String token) {

        String guestIdFromToken = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        if (guestIdFromToken == null) {
            throw new RuntimeException("User information not found in the token.");
        }
        Integer guestId = Integer.parseInt(guestIdFromToken);
        
        Optional<Booking> existingBooking = bookingRepo.findByRequestId(request.getRequestId());
        if (existingBooking.isPresent()) {
            System.out.println("Booking request with requestId: " + request.getRequestId() + " already processed. Returning existing booking.");
            return existingBooking.get();
        }

        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new UserNotFoundException("Guest not found"));

        Room room = roomRepo.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getAvailability() < request.getNumberOfRooms()) {
            throw new RoomUnavailableException("Room not available");
        }

        Integer numDays = (int) ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());

        if (numDays <= 0) {
            throw new IllegalArgumentException("Checkout date must be after check-in date.");
        }

        double baseAmount = request.getNumberOfRooms() * numDays * room.getPrice();
        double payableAmount = baseAmount;
        double redeemedAmount = 0;

        if (request.isUseLoyaltyPoints()) {
            redeemedAmount = loyaltyService.redeemPoints(guest.getGuestId(), baseAmount);
            payableAmount = baseAmount - redeemedAmount;
        }

        Payment payment = paymentService.createPayment(guest.getGuestId(), payableAmount, request.getPaymentMethod(), request.getRequestId());

        if (payment.getStatus() == PaymentStatus.FAILED) {
            LoyaltyPoints account = loyaltyRepository.findById(guest.getGuestId()).orElse(null);
            if (account != null) {
                account.setPoints(account.getPoints() + (int) redeemedAmount);
                account.setLastUpdated(LocalDateTime.now());
                loyaltyRepository.save(account);
            }
        }

        if (payment.getStatus().equals(PaymentStatus.SUCCESS)) {
            room.setAvailability(room.getAvailability() - request.getNumberOfRooms());
            roomRepo.save(room);
        }

        guest.setHotelBooked(room.getHotel());
        guestRepo.save(guest);

        Booking booking = Booking.builder()
                .guest(guest)
                .room(room)
                .roomType(room.getType().name())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfRooms(request.getNumberOfRooms())
                .useLoyaltyPoints(request.isUseLoyaltyPoints())
                .amount(payableAmount)
                .status(payment.getStatus().equals(PaymentStatus.SUCCESS) ? BookingStatus.CONFIRMED : BookingStatus.FAILED)
                .payment(payment)
                .requestId(request.getRequestId())
                .bookingDate(LocalDateTime.now())
                .build();

        booking = bookingRepo.save(booking);

        if (payment.getStatus().equals(PaymentStatus.SUCCESS)) {
            loyaltyService.addPoints(guest.getGuestId(), (int) (payment.getAmount() / 10));
        }

        return booking;
    }

    @Override
    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            Room room = roomRepo.findById(booking.getRoom().getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
            room.setAvailability(room.getAvailability() + booking.getNumberOfRooms());
            roomRepo.save(room);
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepo.save(booking);
        }
    }

    @Override
    public List<BookingDetailsDTO> getBookingsByUser(Integer guestId) {
        return bookingRepo.findByGuest_GuestIdAndStatus(guestId, BookingStatus.CONFIRMED)
                .stream()
                .map(this::convertToBookingDetailsDTO)
                .collect(Collectors.toList());
    }


    @Override
    public Booking getBookingById(Integer bookingId) {
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Override
    public List<BookingDetailsDTO> getConfirmedBookingsForManager() {
        return bookingRepo.findByStatus(BookingStatus.CONFIRMED)
                .stream()
                .map(this::convertToBookingDetailsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDetailsDTO> getAllBookings() {
        return bookingRepo.findAll()
                .stream()
                .map(this::convertToBookingDetailsDTO)
                .collect(Collectors.toList());
    }
    private BookingDetailsDTO convertToBookingDetailsDTO(Booking booking) {
        return BookingDetailsDTO.builder()
                .bookingId(booking.getBookingId())
                .guestId(booking.getGuest().getGuestId())
                .guestName(booking.getGuest().getGuestName())
                .roomId(booking.getRoom().getRoomId())
                .roomType(booking.getRoom().getType())
                .hotelName(booking.getRoom().getHotel().getHotelName())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfRooms(booking.getNumberOfRooms())
                .status(booking.getStatus())
                .paymentId(booking.getPayment() != null ? booking.getPayment().getPaymentId() : null)
                .paymentStatus(booking.getPayment() != null ? booking.getPayment().getStatus() : null)
                .paymentMethod(booking.getPayment() != null ? booking.getPayment().getMethod() : null)
                .amountPaid(booking.getPayment() != null ? booking.getPayment().getAmount() : 0.0)
                .build();
    }

    public boolean hasGuestBookedHotelWithConfirmedStatus(Integer hotelId, String token) {
        String guestIdFromToken = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        if (guestIdFromToken == null) {
            return false;
        }
        Integer guestId = Integer.parseInt(guestIdFromToken);
        List<Booking> confirmedBookings = bookingRepo.findByGuest_GuestIdAndRoom_Hotel_HotelIdAndStatus(
                guestId, hotelId, BookingStatus.CONFIRMED
        );
        return !confirmedBookings.isEmpty();
    }
}