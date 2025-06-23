package com.hotel.Booking_System.repository.loyaltyRepo;

import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.LoyaltyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyRepository extends JpaRepository<LoyaltyPoints,Integer> {
    Optional<LoyaltyPoints> findByGuest(Guest guest);
}
