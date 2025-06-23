package com.hotel.Booking_System.dto.userdto;

import com.hotel.Booking_System.dto.bookingdto.GuestBookingDTO;
import com.hotel.Booking_System.dto.bookingdto.GuestPaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestDTO {
    private Integer guestId;
    private String guestName;
    private String guestUserName;
    private String guestEmail;
    private String guestContact;
    private Integer hotelId;
    private List<GuestBookingDTO> bookingHistory;
    private List<GuestPaymentDTO> paymentHistory;


}
