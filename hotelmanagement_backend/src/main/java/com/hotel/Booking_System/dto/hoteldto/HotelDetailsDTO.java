package com.hotel.Booking_System.dto.hoteldto;

import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.dto.reviewdto.ReviewDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDetailsDTO {

//    private Integer hotelId;
    private String hotelName;
    private String location;
    private String amenities;
    //    private Integer managerId;
//    private String managerName;
    private String imageUrl;
    private String description;
//    List<ReviewDetailsDTO> reviews;

}
