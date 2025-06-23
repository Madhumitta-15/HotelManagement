package com.hotel.Booking_System.service.userService;


import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.model.Guest;

public interface GuestService {
    Guest register(Guest newGuest);
    void deleteGuest(String guestName);
    UserRepresentationDTO viewGuest(String guestname);
    GuestDTO guestProfile(String guestName);
    Guest updateGuest(String guestname, UserUpdateDTO newGuest);
    void updateGuestById(Integer guestId, UserUpdateDTO newGuest);

}

