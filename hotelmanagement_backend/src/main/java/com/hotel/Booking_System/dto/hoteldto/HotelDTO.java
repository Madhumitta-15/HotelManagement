package com.hotel.Booking_System.dto.hoteldto;

import com.hotel.Booking_System.dto.userdto.ManagerDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {

    private int hotelId;
    private String hotelName;
    private String location;
    private String amenities;
    private String description;
//    private Integer managerId;
//    private String managerName;
    private String imageUrl;
    private ManagerDTO manager;
}

