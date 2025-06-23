package com.hotel.Booking_System.service.loyaltyService;

import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.LoyaltyPoints;
import com.hotel.Booking_System.repository.loyaltyRepo.LoyaltyRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService{


    private final LoyaltyRepository loyaltyRepository;
    private final GuestRepository guestRepository;


    @Override
    public double redeemPoints(Integer guestId, double amount) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        LoyaltyPoints account = loyaltyRepository.findByGuest(guest).orElseGet(() -> {
            LoyaltyPoints acc = LoyaltyPoints.builder()
                    .guest(guest)
                    .points(0)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            return acc;
        });

        int redeemable = Math.min(account.getPoints(), (int) amount);
        account.setPoints(account.getPoints() - redeemable);
        account.setLastUpdated(LocalDateTime.now());
        loyaltyRepository.save(account);
        return (double) redeemable;
    }

    @Override
    public void addPoints(Integer guestId, int points) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        LoyaltyPoints account = loyaltyRepository.findByGuest(guest).orElseGet(() -> {
            LoyaltyPoints acc = LoyaltyPoints.builder()
                    .guest(guest)
                    .points(0)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            return acc;
        });

        account.setPoints(account.getPoints() + points);
        account.setLastUpdated(LocalDateTime.now());
        loyaltyRepository.save(account);
    }

    @Override
    public int getAvailablePoints(Integer guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        LoyaltyPoints loyaltyPoints = loyaltyRepository.findByGuest(guest)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty points not found for guest"));
        return loyaltyPoints.getPoints();
    }





}
