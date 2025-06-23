package com.hotel.Booking_System.repository.bookRepo;

import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByRequestId(String requestId);
    List<Booking> findByGuest_GuestIdAndStatus(Integer guestId, BookingStatus status);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByGuest_GuestIdAndRoom_Hotel_HotelIdAndStatus(Integer guestId, Integer hotelId, BookingStatus status);
}