package com.hotel.Booking_System.service.userService;



import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.Manager;

import java.util.List;


public interface AdminService {
     List<GuestDTO> viewAllGuests();
    List<Manager> viewAllManagers();

}

