package com.hotel.Booking_System.controller.booking;

import com.hotel.Booking_System.dto.bookingdto.BookingDetailsDTO;
import com.hotel.Booking_System.dto.bookingdto.BookingRequestDTO;
import com.hotel.Booking_System.dto.bookingdto.BookingResponseDTO;
import com.hotel.Booking_System.dto.bookingdto.PaymentResponseDTO;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.RoomUnavailableException;
import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.enums.PaymentStatus;
import com.hotel.Booking_System.service.bookServices.BookingService;
import com.hotel.Booking_System.service.reviewService.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final EmailServiceImpl emailService;

    @PostMapping("/bookroom")
    public ResponseEntity<?> bookRoom(@RequestBody BookingRequestDTO bookingRequestDTO,
                                      @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid.");
            }
            String token = authorizationHeader.substring(7);

            Booking booking = bookingService.bookRoom(bookingRequestDTO, token);

            PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
                    .paymentId(booking.getPayment().getPaymentId())
                    .guestId(booking.getPayment().getGuest().getGuestId())
                    .amount(booking.getPayment().getAmount())
                    .method(booking.getPayment().getMethod())
                    .status(PaymentStatus.valueOf(booking.getPayment().getStatus().name())) // Convert enum to String name
                    .build();

            BookingResponseDTO bookingResponseDTO = BookingResponseDTO.builder()
                    .bookingId(booking.getBookingId())
                    .roomId(booking.getRoom().getRoomId())
                    .checkInDate(booking.getCheckInDate())
                    .checkOutDate(booking.getCheckOutDate())
                    .roomType(booking.getRoom().getType())
                    .numberOfRooms(booking.getNumberOfRooms())
                    .payment(paymentResponseDTO)
                    .useLoyaltyPoints(booking.isUseLoyaltyPoints())
                    .build();

            if (booking.getPayment().getStatus() == PaymentStatus.SUCCESS) {
                emailService.sendEmail(booking.getGuest().getGuestEmail(),
                        "Booking Confirmation: Your Room Awaits!",
                        "Dear " + booking.getGuest().getGuestName() + ",\n\n" +
                                "Your booking (ID: " + booking.getBookingId() + ") at " + booking.getRoom().getHotel().getHotelName() + " has been successfully confirmed!\n\n" +
                                "Details:\n" +
                                "  Room Type: " + booking.getRoomType() + "\n" +
                                "  Check-in: " + booking.getCheckInDate() + "\n" +
                                "  Check-out: " + booking.getCheckOutDate() + "\n" +
                                "  Number of Rooms: " + booking.getNumberOfRooms() + "\n" +
                                "  Total Paid: ₹" + String.format("%.2f", booking.getPayment().getAmount()) + "\n\n" +
                                "We look forward to welcoming you! Have a great experience.\n\n" +
                                "Thank you for choosing us!\n" +
                                "The Hotel Team"
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponseDTO);
            } else if (booking.getPayment().getStatus() == PaymentStatus.FAILED) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bookingResponseDTO);
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking process completed with an unexpected payment status.");
            }

        } catch (ResourceNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + e.getMessage());
        } catch (RoomUnavailableException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Room unavailable: " + e.getMessage());
        } catch (Exception e) {

            System.err.println("Error during room booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during booking: " + e.getMessage());
        }
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<BookingDetailsDTO>> getBookingsByUser(@PathVariable Integer guestId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(guestId));
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Integer bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/confirmedbookings")
    public ResponseEntity<List<BookingDetailsDTO>> getConfirmedBookingsForManager() {
        return ResponseEntity.ok(bookingService.getConfirmedBookingsForManager());
    }

    @GetMapping("/check-review-eligibility/{hotelId}")
    public ResponseEntity<Map<String, Boolean>> checkReviewEligibility(
            @PathVariable Integer hotelId,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("canReviewHotel", false));
        }
        String token = authorizationHeader.substring(7);
        boolean canReview = bookingService.hasGuestBookedHotelWithConfirmedStatus(hotelId, token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("canReviewHotel", canReview);
        return ResponseEntity.ok(response);
    }
}