package com.hotel.Booking_System.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailsDTO {

    private String comment;
    private Integer rating;
    private Date date; // Date of the review
}
