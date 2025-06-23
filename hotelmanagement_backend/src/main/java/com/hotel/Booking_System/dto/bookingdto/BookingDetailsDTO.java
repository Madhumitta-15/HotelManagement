package com.hotel.Booking_System.dto.bookingdto;


import com.hotel.Booking_System.model.enums.BookingStatus;
import com.hotel.Booking_System.model.enums.PaymentMethod;
import com.hotel.Booking_System.model.enums.PaymentStatus;
import com.hotel.Booking_System.model.enums.RoomType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingDetailsDTO {
    private Integer bookingId;
    private Integer guestId;
    private String guestName;
    private Integer roomId;
    private RoomType roomType;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfRooms;
    private BookingStatus status;
    private Integer paymentId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private Double amountPaid;
}

