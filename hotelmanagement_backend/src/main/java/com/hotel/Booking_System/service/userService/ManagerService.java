package com.hotel.Booking_System.service.userService;


import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Manager;


public interface ManagerService {
    void addHotelManager(Manager manager);
    void deleteManager(int managerId);
    Manager updateManager(int managerId, UserUpdateDTO updatedManager);
    UserRepresentationDTO viewHotelManager(int managerId);
    Hotel getHotelByManagerId(int managerId);
}

