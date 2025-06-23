package com.hotel.Booking_System.dto.bookingdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GuestPaymentDTO {
    private Integer paymentId;
    private Double amount;
}
