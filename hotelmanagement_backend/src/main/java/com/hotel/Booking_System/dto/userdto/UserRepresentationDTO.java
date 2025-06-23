package com.hotel.Booking_System.dto.userdto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRepresentationDTO {

    private int id;
    private String name;
    private String email;
    private String contact;

}

