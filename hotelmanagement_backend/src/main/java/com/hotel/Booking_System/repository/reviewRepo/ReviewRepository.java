package com.hotel.Booking_System.repository.reviewRepo;

import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    @Query("SELECT r FROM Review r JOIN FETCH r.guest g WHERE r.hotel.hotelId = :hotelId")
    List<Review> findByHotel_HotelId(Integer hotelId);
    @Query("SELECT r FROM Review r JOIN r.hotel h WHERE h.hotelName = :hotelName")
    List<Review> findByHotel_HotelName(@Param("hotelName") String hotelName);
    List<Review> findByHotel(Hotel hotel);
}
