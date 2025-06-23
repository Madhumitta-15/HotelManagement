package com.hotel.Booking_System.service.bookServices;

import com.hotel.Booking_System.dto.bookingdto.BookingDetailsDTO;
import com.hotel.Booking_System.dto.bookingdto.BookingRequestDTO;
import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    Booking bookRoom(BookingRequestDTO request,String token) ;
    void cancelBooking(Integer bookingId);
    List<BookingDetailsDTO> getBookingsByUser(Integer guestId);
    Booking getBookingById(Integer bookingId);
    List<BookingDetailsDTO> getConfirmedBookingsForManager();
    List<BookingDetailsDTO> getAllBookings();
    boolean hasGuestBookedHotelWithConfirmedStatus(Integer hotelId, String token);
}
