package com.hotel.Booking_System.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Integer reviewId;
    private String guestUserName;
    private String comment;
    private Integer rating;
    private String reviewDate;
    private Integer hotelId;
    private String category;
}