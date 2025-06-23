package com.hotel.Booking_System.dto.bookingdto;


import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.enums.PaymentMethod;
import com.hotel.Booking_System.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class PaymentResponseDTO {

    private Integer paymentId;
    private Integer guestId;
    private double amount;
    private PaymentStatus status;

    private PaymentMethod method;

}

