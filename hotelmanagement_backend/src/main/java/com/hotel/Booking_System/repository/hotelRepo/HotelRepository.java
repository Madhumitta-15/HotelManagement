package com.hotel.Booking_System.repository.hotelRepo;

import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.enums.RoomType; // Import the RoomType enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    Optional<Hotel> findByHotelNameAndLocation(String hotelName, String location);
    List<Hotel> findByLocationIgnoreCase(String location);

    // Existing method to find hotels by room type
    @Query("SELECT h FROM Hotel h JOIN h.rooms r WHERE r.type = :roomType")
    List<Hotel> findHotelsByRoomType(@Param("roomType") RoomType roomType);

    // NEW METHOD: Find hotels by location and room type
    @Query("SELECT DISTINCT h FROM Hotel h JOIN h.rooms r WHERE LOWER(h.location) = LOWER(:location) AND r.type = :roomType")
    List<Hotel> findHotelsByLocationAndRoomType(@Param("location") String location, @Param("roomType") RoomType roomType);
}