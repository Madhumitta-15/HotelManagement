package com.hotel.Booking_System.repository.userRepo;

import com.hotel.Booking_System.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest,Integer> {
    Optional<Guest> findByGuestName(String guestName);
    Optional<Guest> findByGuestEmail(String guestEmail);
    Optional<Guest> findByGuestContact(String guestContact);
    Optional<Guest> findByGuestUserName(String guestUserName);
}
