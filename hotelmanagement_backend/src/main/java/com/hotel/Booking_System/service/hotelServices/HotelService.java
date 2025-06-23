package com.hotel.Booking_System.service.hotelServices;

import com.hotel.Booking_System.dto.hoteldto.HotelDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelDetailsDTO; // Assuming you're using this DTO for details
import com.hotel.Booking_System.dto.hoteldto.HotelResponseDTO;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.model.enums.RoomType;

import java.util.List;
import java.util.Optional;

public interface HotelService {
    List<HotelDTO> getAllHotels();
    Optional<Hotel> getHotelById(Integer hotelId);
    void addHotel(HotelDTO hotelDTO);
    Hotel updateHotel(Integer hotelId, HotelDTO hotelRequestDTO);
    void deleteHotel(int hotelId);
    List<HotelResponseDTO> getHotelsByLocation(String location);
    List<HotelDetailsDTO> getHotelsByRoomType(RoomType roomType); // Existing
    Review addReviewToHotel(Integer hotelId, Review review);
    Hotel assignManagerToHotel(int hotelId, Integer managerId);
    HotelDTO viewHotelByManagerId(int managerId);

    // NEW METHOD: Filter hotels by location and room type
    List<HotelDetailsDTO> getHotelsByLocationAndRoomType(String location, RoomType roomType);

    HotelDetailsDTO gethoteldetails(Integer hotelId);
}