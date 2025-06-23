package com.hotel.Booking_System.dto.hoteldto;

import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.model.Review;
import lombok.Data;

import java.util.List;

@Data
public class HotelResponseDTO {
    private Integer hotelId;
    private String hotelName;
    private String location;
    private String amenities;
    private String imageUrl;
    private List<ReviewDTO> reviews;
}
