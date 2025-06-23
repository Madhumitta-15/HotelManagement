package com.hotel.Booking_System.dto.bookingdto;

import com.hotel.Booking_System.model.enums.RoomType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    @NotNull
    private Integer bookingId;
    @NotNull
    private Integer guestId;

    @NotNull
    private Integer roomId;

    @NotNull
    private RoomType roomType;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @Min(1)
    private int numberOfRooms;

    private boolean useLoyaltyPoints;

    @NotNull
    private PaymentResponseDTO payment;



}

