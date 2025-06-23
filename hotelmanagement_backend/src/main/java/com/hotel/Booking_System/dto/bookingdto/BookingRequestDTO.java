package com.hotel.Booking_System.dto.bookingdto;

import com.hotel.Booking_System.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfRooms;
    private PaymentMethod paymentMethod;
    private boolean useLoyaltyPoints;
    private double totalPrice;
    private String requestId;
}