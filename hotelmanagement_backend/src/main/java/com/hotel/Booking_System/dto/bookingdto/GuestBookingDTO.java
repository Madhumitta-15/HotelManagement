package com.hotel.Booking_System.dto.bookingdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GuestBookingDTO {
    private Integer bookingId;
    private String roomType;
}
