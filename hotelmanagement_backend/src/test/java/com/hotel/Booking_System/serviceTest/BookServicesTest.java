package com.hotel.Booking_System.serviceTest;

import com.hotel.Booking_System.dto.bookingdto.BookingDetailsDTO;
import com.hotel.Booking_System.dto.bookingdto.BookingRequestDTO;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.exception.RoomUnavailableException;
import com.hotel.Booking_System.model.*;
import com.hotel.Booking_System.model.enums.BookingStatus;
import com.hotel.Booking_System.model.enums.PaymentMethod;
import com.hotel.Booking_System.model.enums.PaymentStatus;
import com.hotel.Booking_System.model.enums.RoomType;
import com.hotel.Booking_System.repository.bookRepo.BookingRepository;
import com.hotel.Booking_System.repository.bookRepo.PaymentRepository;
import com.hotel.Booking_System.repository.hotelRepo.RoomRepository;
import com.hotel.Booking_System.repository.loyaltyRepo.LoyaltyRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.bookServices.BookingServiceImpl;
import com.hotel.Booking_System.service.bookServices.PaymentService;
import com.hotel.Booking_System.service.bookServices.PaymentServiceImpl;
import com.hotel.Booking_System.service.loyaltyService.LoyaltyService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServicesTest {

    @Nested
    class BookingServicesTest {

        @Mock
        private BookingRepository bookingRepo;
        @Mock
        private RoomRepository roomRepo;
        @Mock
        private GuestRepository guestRepo;
        @Mock
        private PaymentService paymentService;
        @Mock
        private LoyaltyService loyaltyService;
        @Mock
        private LoyaltyRepository loyaltyRepository;
        @Mock
        private JWTServiceImpl jwtService;

        @InjectMocks
        private BookingServiceImpl bookingService;

        private Guest guest;
        private Room room;
        private BookingRequestDTO bookingRequestDTO;
        private Payment payment;
        private Hotel hotel;

        @BeforeEach
        void setUp() {
            hotel = new Hotel();
            hotel.setHotelId(1);
            hotel.setHotelName("Grand Hyatt");

            guest = new Guest();
            guest.setGuestId(1);
            guest.setGuestName("John Doe");
            guest.setHotelBooked(null);

            room = new Room();
            room.setRoomId(1);
            room.setType(RoomType.DELUXE);
            room.setPrice(100.0);
            room.setAvailability(5);
            room.setHotel(hotel);

            bookingRequestDTO = new BookingRequestDTO();
            bookingRequestDTO.setRoomId(1);
            bookingRequestDTO.setCheckInDate(LocalDate.now().plusDays(1));
            bookingRequestDTO.setCheckOutDate(LocalDate.now().plusDays(3));
            bookingRequestDTO.setNumberOfRooms(1);
            bookingRequestDTO.setUseLoyaltyPoints(false);
            bookingRequestDTO.setPaymentMethod(PaymentMethod.CARD);
            bookingRequestDTO.setRequestId("req123");

            payment = new Payment();
            payment.setPaymentId(1);
            payment.setGuest(guest);
            payment.setAmount(200.0);
            payment.setMethod(PaymentMethod.CARD);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setRequestId("req123");
        }

        @Test
        void bookRoom_Success_NewBooking() {
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.of(guest));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.of(room));
            when(paymentService.createPayment(anyInt(), anyDouble(), any(PaymentMethod.class), anyString()))
                    .thenReturn(payment);
            when(roomRepo.save(any(Room.class))).thenReturn(room);
            when(guestRepo.save(any(Guest.class))).thenReturn(guest);
            when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(loyaltyService).addPoints(anyInt(), anyInt());

            Booking result = bookingService.bookRoom(bookingRequestDTO, "dummyToken");

            assertNotNull(result);
            assertEquals(BookingStatus.CONFIRMED, result.getStatus());
            assertEquals(4, room.getAvailability()); // Availability decreased
            assertEquals(hotel, guest.getHotelBooked()); // Hotel booked set
            verify(loyaltyService, times(1)).addPoints(anyInt(), anyInt());
            verify(roomRepo, times(1)).save(room);
            verify(guestRepo, times(1)).save(guest);
            verify(bookingRepo, times(1)).save(any(Booking.class));
        }

        @Test
        void bookRoom_Success_ExistingBookingWithSameRequestId() {
            Booking existingBooking = new Booking();
            existingBooking.setBookingId(1);
            existingBooking.setRequestId("req123");
            existingBooking.setStatus(BookingStatus.CONFIRMED);

            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.of(existingBooking));

            Booking result = bookingService.bookRoom(bookingRequestDTO, "dummyToken");

            assertNotNull(result);
            assertEquals(existingBooking.getBookingId(), result.getBookingId());
            verify(bookingRepo, times(1)).findByRequestId("req123");
            verify(guestRepo, never()).findById(anyInt());
            verify(roomRepo, never()).findById(anyInt());
        }

        @Test
        void bookRoom_GuestNotFound() {
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> bookingService.bookRoom(bookingRequestDTO, "dummyToken"));
        }

        @Test
        void bookRoom_RoomNotFound() {
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.of(guest));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> bookingService.bookRoom(bookingRequestDTO, "dummyToken"));
        }

        @Test
        void bookRoom_RoomUnavailable() {
            room.setAvailability(0);
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.of(guest));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.of(room));

            assertThrows(RoomUnavailableException.class,
                    () -> bookingService.bookRoom(bookingRequestDTO, "dummyToken"));
        }

        @Test
        void bookRoom_InvalidDates() {
            bookingRequestDTO.setCheckOutDate(LocalDate.now().plusDays(1));
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.of(guest));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.of(room));

            assertThrows(IllegalArgumentException.class,
                    () -> bookingService.bookRoom(bookingRequestDTO, "dummyToken"));
        }

        @Test
        void bookRoom_PaymentFailed() {
            payment.setStatus(PaymentStatus.FAILED);
            LoyaltyPoints loyaltyPoints = new LoyaltyPoints();
            loyaltyPoints.setId(1);
            loyaltyPoints.setPoints(100);

            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.of(guest));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.of(room));
            when(paymentService.createPayment(anyInt(), anyDouble(), any(PaymentMethod.class), anyString()))
                    .thenReturn(payment);
            when(loyaltyRepository.findById(anyInt())).thenReturn(Optional.of(loyaltyPoints));
            when(loyaltyRepository.save(any(LoyaltyPoints.class))).thenReturn(loyaltyPoints);
            when(guestRepo.save(any(Guest.class))).thenReturn(guest);
            when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Booking result = bookingService.bookRoom(bookingRequestDTO, "dummyToken");

            assertNotNull(result);
            assertEquals(BookingStatus.FAILED, result.getStatus());
            assertEquals(5, room.getAvailability());
            verify(loyaltyService, never()).addPoints(anyInt(), anyInt());
            verify(loyaltyRepository, times(1)).save(any(LoyaltyPoints.class));
        }

        @Test
        void bookRoom_WithLoyaltyPoints() {
            bookingRequestDTO.setUseLoyaltyPoints(true);
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepo.findById(anyInt())).thenReturn(Optional.of(guest));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.of(room));
            when(loyaltyService.redeemPoints(anyInt(), anyDouble())).thenReturn(50.0);
            when(paymentService.createPayment(anyInt(), eq(150.0), any(PaymentMethod.class), anyString()))
                    .thenReturn(payment);
            when(roomRepo.save(any(Room.class))).thenReturn(room);
            when(guestRepo.save(any(Guest.class))).thenReturn(guest);
            when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(loyaltyService).addPoints(anyInt(), anyInt());

            Booking result = bookingService.bookRoom(bookingRequestDTO, "dummyToken");

            assertNotNull(result);
            assertEquals(BookingStatus.CONFIRMED, result.getStatus());
            assertEquals(4, room.getAvailability());
            verify(loyaltyService, times(1)).redeemPoints(guest.getGuestId(), 200.0);
            verify(paymentService, times(1)).createPayment(guest.getGuestId(), 150.0, PaymentMethod.CARD, "req123");
            verify(loyaltyService, times(1)).addPoints(anyInt(), anyInt());
        }

        @Test
        void cancelBooking_Success() {
            Booking booking = new Booking();
            booking.setBookingId(1);
            booking.setRoom(room);
            booking.setNumberOfRooms(1);
            booking.setStatus(BookingStatus.CONFIRMED);

            when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));
            when(roomRepo.findById(anyInt())).thenReturn(Optional.of(room));
            when(roomRepo.save(any(Room.class))).thenReturn(room);
            when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

            bookingService.cancelBooking(1);

            assertEquals(BookingStatus.CANCELLED, booking.getStatus());
            assertEquals(6, room.getAvailability());
            verify(roomRepo, times(1)).save(room);
            verify(bookingRepo, times(1)).save(booking);
        }

        @Test
        void cancelBooking_NotFound() {
            when(bookingRepo.findById(anyInt())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking(1));
        }

        @Test
        void cancelBooking_AlreadyCancelled() {
            Booking booking = new Booking();
            booking.setBookingId(1);
            booking.setRoom(room);
            booking.setNumberOfRooms(1);
            booking.setStatus(BookingStatus.CANCELLED);

            when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));

            bookingService.cancelBooking(1);

            assertEquals(BookingStatus.CANCELLED, booking.getStatus());
            verify(roomRepo, never()).save(any(Room.class));
            verify(bookingRepo, never()).save(any(Booking.class));
        }

        @Test
        void getBookingsByUser_Success() {
            Booking booking = new Booking();
            booking.setBookingId(1);
            booking.setGuest(guest);
            booking.setRoom(room);
            booking.setCheckInDate(LocalDate.now());
            booking.setCheckOutDate(LocalDate.now().plusDays(2));
            booking.setNumberOfRooms(1);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPayment(payment);

            when(bookingRepo.findByGuest_GuestIdAndStatus(anyInt(), eq(BookingStatus.CONFIRMED)))
                    .thenReturn(List.of(booking));

            List<BookingDetailsDTO> result = bookingService.getBookingsByUser(1);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(booking.getBookingId(), result.get(0).getBookingId());
            assertEquals(booking.getGuest().getGuestId(), result.get(0).getGuestId());
        }

        @Test
        void getBookingsByUser_NoBookings() {
            when(bookingRepo.findByGuest_GuestIdAndStatus(anyInt(), eq(BookingStatus.CONFIRMED)))
                    .thenReturn(Collections.emptyList());

            List<BookingDetailsDTO> result = bookingService.getBookingsByUser(1);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void getAllBookings_Success() {
            Booking booking = new Booking();
            booking.setBookingId(1);
            booking.setGuest(guest);
            booking.setRoom(room);
            booking.setCheckInDate(LocalDate.now());
            booking.setCheckOutDate(LocalDate.now().plusDays(2));
            booking.setNumberOfRooms(1);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPayment(payment);

            when(bookingRepo.findAll()).thenReturn(List.of(booking));

            List<BookingDetailsDTO> result = bookingService.getAllBookings();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(booking.getBookingId(), result.get(0).getBookingId());
        }

        @Test
        void getAllBookings_NoBookings() {
            when(bookingRepo.findAll()).thenReturn(Collections.emptyList());

            List<BookingDetailsDTO> result = bookingService.getAllBookings();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void getBookingById_Success() {
            Booking booking = new Booking();
            booking.setBookingId(1);
            when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));

            Booking result = bookingService.getBookingById(1);

            assertNotNull(result);
            assertEquals(1, result.getBookingId());
        }

        @Test
        void getBookingById_NotFound() {
            when(bookingRepo.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1));
        }

        @Test
        void getConfirmedBookingsForManager_Success() {
            Booking booking = new Booking();
            booking.setBookingId(1);
            booking.setGuest(guest);
            booking.setRoom(room);
            booking.setCheckInDate(LocalDate.now());
            booking.setCheckOutDate(LocalDate.now().plusDays(2));
            booking.setNumberOfRooms(1);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPayment(payment);

            when(bookingRepo.findByStatus(BookingStatus.CONFIRMED)).thenReturn(List.of(booking));

            List<BookingDetailsDTO> result = bookingService.getConfirmedBookingsForManager();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(booking.getBookingId(), result.get(0).getBookingId());
        }

        @Test
        void getConfirmedBookingsForManager_NoBookings() {
            when(bookingRepo.findByStatus(BookingStatus.CONFIRMED)).thenReturn(Collections.emptyList());

            List<BookingDetailsDTO> result = bookingService.getConfirmedBookingsForManager();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void hasGuestBookedHotelWithConfirmedStatus_True() {
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByGuest_GuestIdAndRoom_Hotel_HotelIdAndStatus(anyInt(), anyInt(),
                    eq(BookingStatus.CONFIRMED)))
                    .thenReturn(List.of(new Booking()));

            assertTrue(bookingService.hasGuestBookedHotelWithConfirmedStatus(1, "token"));
        }

        @Test
        void hasGuestBookedHotelWithConfirmedStatus_False_NoBookings() {
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn("1");
            when(bookingRepo.findByGuest_GuestIdAndRoom_Hotel_HotelIdAndStatus(anyInt(), anyInt(),
                    eq(BookingStatus.CONFIRMED)))
                    .thenReturn(Collections.emptyList());
            assertFalse(bookingService.hasGuestBookedHotelWithConfirmedStatus(1, "token"));
        }

        @Test
        void hasGuestBookedHotelWithConfirmedStatus_False_InvalidToken() {
            when(jwtService.extractClaim(anyString(), any(Function.class))).thenReturn(null);
            assertFalse(bookingService.hasGuestBookedHotelWithConfirmedStatus(1, "invalidToken"));
        }
    }

    @Nested
    class PaymentServiceImplTest {
        @Mock
        private PaymentRepository paymentRepository;
        @Mock
        private GuestRepository guestRepository;

        @InjectMocks
        private PaymentServiceImpl paymentService;

        private Guest guest;
        private Payment payment;

        @BeforeEach
        void setUp() {
            guest = new Guest();
            guest.setGuestId(1);
            guest.setGuestName("Jane Doe");

            payment = new Payment();
            payment.setPaymentId(1);
            payment.setGuest(guest);
            payment.setAmount(150.0);
            payment.setMethod(PaymentMethod.CARD);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setRequestId("paymentReq123");
        }

        @Test
        void createPayment_Success_NewPayment() {
            when(paymentRepository.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment savedPayment = invocation.getArgument(0);
                savedPayment.setStatus(PaymentStatus.SUCCESS);
                return savedPayment;
            });

            Payment result = paymentService.createPayment(1, 100.0, PaymentMethod.CARD, "newReq456");

            assertNotNull(result);
            assertEquals(PaymentStatus.SUCCESS, result.getStatus());
            assertEquals("newReq456", result.getRequestId());
            verify(paymentRepository, times(1)).save(any(Payment.class));
        }

        @Test
        void createPayment_Success_ExistingPaymentWithSameRequestId() {
            when(paymentRepository.findByRequestId(anyString())).thenReturn(Optional.of(payment));

            Payment result = paymentService.createPayment(1, 100.0, PaymentMethod.CARD, "paymentReq123");

            assertNotNull(result);
            assertEquals(payment.getPaymentId(), result.getPaymentId());
            assertEquals("paymentReq123", result.getRequestId());
            verify(paymentRepository, times(1)).findByRequestId("paymentReq123");
            verify(guestRepository, never()).findById(anyInt());
            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        void createPayment_GuestNotFound() {
            when(paymentRepository.findByRequestId(anyString())).thenReturn(Optional.empty());
            when(guestRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.createPayment(1, 100.0, PaymentMethod.CARD, "req789"));
        }

        @Test
        void getPaymentDetails_Success() {
            when(paymentRepository.findById(anyInt())).thenReturn(Optional.of(payment));
            Payment result = paymentService.getPaymentDetails(1);
            assertNotNull(result);
            assertEquals(1, result.getPaymentId());
        }

        @Test
        void getPaymentDetails_NotFound() {
            when(paymentRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentDetails(99));
        }
    }
}