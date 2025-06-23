package com.hotel.Booking_System.service.userService;

import com.hotel.Booking_System.dto.bookingdto.GuestBookingDTO;
import com.hotel.Booking_System.dto.bookingdto.GuestPaymentDTO;
import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.Manager;

import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public List<GuestDTO> viewAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        return guests.stream()
                .map(g -> new GuestDTO(
                        g.getGuestId(),
                        g.getGuestName(),
                        g.getGuestUserName(),
                        g.getGuestEmail(),
                        g.getGuestContact(),
                        g.getHotelBooked() != null ? g.getHotelBooked().getHotelId() : null,
                        g.getBookings().stream()
                                .map(b -> new GuestBookingDTO(b.getBookingId(), b.getRoomType()))
                                .toList(),
                        g.getPayments().stream()
                                .map(p -> new GuestPaymentDTO(p.getPaymentId(), p.getAmount()))
                                .toList()
                        ))
                .toList();

    }

    @Override
    public List<Manager> viewAllManagers() {
        return managerRepository.findAll();
    }
}

