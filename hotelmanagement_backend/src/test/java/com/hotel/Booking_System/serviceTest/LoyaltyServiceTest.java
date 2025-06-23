package com.hotel.Booking_System.serviceTest;

import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.LoyaltyPoints;
import com.hotel.Booking_System.repository.loyaltyRepo.LoyaltyRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.loyaltyService.LoyaltyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

    @Mock
    private LoyaltyRepository loyaltyRepository;
    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    private Guest guest;
    private LoyaltyPoints loyaltyPointsAccount;

    @BeforeEach
    void setUp() {
        // Initialize a Guest object using setters
        guest = new Guest();
        guest.setGuestId(1);
        guest.setGuestName("Test Guest");

        // Initialize a LoyaltyPoints object using setters
        loyaltyPointsAccount = new LoyaltyPoints();
        loyaltyPointsAccount.setId(1); // Assuming an ID for the loyalty account itself
        loyaltyPointsAccount.setGuest(guest);
        loyaltyPointsAccount.setPoints(100);
        loyaltyPointsAccount.setLastUpdated(LocalDateTime.now());
    }

    @Test
    void redeemPoints_Success_EnoughPoints() {
        // Mocking behavior for dependencies
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.of(loyaltyPointsAccount));
        when(loyaltyRepository.save(any(LoyaltyPoints.class))).thenReturn(loyaltyPointsAccount);

        // Call the method under test
        double redeemed = loyaltyService.redeemPoints(guest.getGuestId(), 50.0);

        // Assertions
        assertEquals(50.0, redeemed, "Should redeem 50 points");
        assertEquals(50, loyaltyPointsAccount.getPoints(), "Remaining points should be 50");
        verify(loyaltyRepository, times(1)).save(loyaltyPointsAccount);
    }

    @Test
    void redeemPoints_Success_NotEnoughPoints() {
        // Set initial points to less than redemption amount
        loyaltyPointsAccount.setPoints(30);

        // Mocking behavior for dependencies
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.of(loyaltyPointsAccount));
        when(loyaltyRepository.save(any(LoyaltyPoints.class))).thenReturn(loyaltyPointsAccount);

        // Call the method under test
        double redeemed = loyaltyService.redeemPoints(guest.getGuestId(), 50.0);

        // Assertions
        assertEquals(30.0, redeemed, "Should redeem only available points (30)");
        assertEquals(0, loyaltyPointsAccount.getPoints(), "Remaining points should be 0");
        verify(loyaltyRepository, times(1)).save(loyaltyPointsAccount);
    }

    @Test
    void redeemPoints_GuestNotFound() {
        // Mocking guestRepository to return empty
        when(guestRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Assert that ResourceNotFoundException is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            loyaltyService.redeemPoints(guest.getGuestId(), 50.0);
        });

        assertEquals("Guest not found", thrown.getMessage());
        verify(loyaltyRepository, never()).findByGuest(any(Guest.class)); // Should not proceed to loyalty repo
        verify(loyaltyRepository, never()).save(any(LoyaltyPoints.class)); // Should not save
    }

    @Test
    void redeemPoints_NoLoyaltyAccount_CreatesNew() {
        // Mocking loyaltyRepository to return empty, simulating no existing account
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.empty());
        // Mock save to capture the newly created LoyaltyPoints object
        when(loyaltyRepository.save(any(LoyaltyPoints.class))).thenAnswer(invocation -> {
            LoyaltyPoints newAccount = invocation.getArgument(0);
            // Simulate setting an ID if it's auto-generated
            if (newAccount.getId() == null) {
                newAccount.setId(2); // Assign a dummy ID for the new account
            }
            return newAccount;
        });

        double redeemed = loyaltyService.redeemPoints(guest.getGuestId(), 10.0);

        assertEquals(0.0, redeemed, "Should redeem 0 points as new account starts with 0");
        // Verify that a new LoyaltyPoints object was created and saved
        verify(loyaltyRepository, times(1)).save(argThat(lp -> lp.getGuest().equals(guest) && lp.getPoints() == 0));
    }


    @Test
    void addPoints_Success_ExistingAccount() {
        // Mocking behavior for dependencies
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.of(loyaltyPointsAccount));
        when(loyaltyRepository.save(any(LoyaltyPoints.class))).thenReturn(loyaltyPointsAccount);

        // Call the method under test
        loyaltyService.addPoints(guest.getGuestId(), 50);

        // Assertions
        assertEquals(150, loyaltyPointsAccount.getPoints(), "Points should increase by 50");
        verify(loyaltyRepository, times(1)).save(loyaltyPointsAccount);
    }

    @Test
    void addPoints_Success_NoExistingAccount_CreatesNew() {
        // Mocking loyaltyRepository to return empty, simulating no existing account
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.empty());
        // Mock save to capture the newly created LoyaltyPoints object
        when(loyaltyRepository.save(any(LoyaltyPoints.class))).thenAnswer(invocation -> {
            LoyaltyPoints newAccount = invocation.getArgument(0);
            if (newAccount.getId() == null) {
                newAccount.setId(2); // Assign a dummy ID
            }
            return newAccount;
        });

        // Call the method under test
        loyaltyService.addPoints(guest.getGuestId(), 50);

        // Assertions
        // Verify that a new LoyaltyPoints object was created with initial 0 points + added 50 points
        verify(loyaltyRepository, times(1)).save(argThat(lp -> lp.getGuest().equals(guest) && lp.getPoints() == 50));
    }

    @Test
    void addPoints_GuestNotFound() {
        // Mocking guestRepository to return empty
        when(guestRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Assert that ResourceNotFoundException is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            loyaltyService.addPoints(guest.getGuestId(), 50);
        });

        assertEquals("Guest not found", thrown.getMessage());
        verify(loyaltyRepository, never()).findByGuest(any(Guest.class));
        verify(loyaltyRepository, never()).save(any(LoyaltyPoints.class));
    }

    @Test
    void getAvailablePoints_Success() {
        // Mocking behavior for dependencies
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.of(loyaltyPointsAccount));

        // Call the method under test
        int availablePoints = loyaltyService.getAvailablePoints(guest.getGuestId());

        // Assertions
        assertEquals(100, availablePoints, "Should return the correct available points");
    }

    @Test
    void getAvailablePoints_GuestNotFound() {
        // Mocking guestRepository to return empty
        when(guestRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Assert that ResourceNotFoundException is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            loyaltyService.getAvailablePoints(guest.getGuestId());
        });

        assertEquals("Guest not found", thrown.getMessage());
        verify(loyaltyRepository, never()).findByGuest(any(Guest.class));
    }

    @Test
    void getAvailablePoints_LoyaltyAccountNotFound() {
        // Mocking guestRepository to return guest, but loyaltyRepository to return empty
        when(guestRepository.findById(anyInt())).thenReturn(Optional.of(guest));
        when(loyaltyRepository.findByGuest(any(Guest.class))).thenReturn(Optional.empty());

        // Assert that ResourceNotFoundException is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            loyaltyService.getAvailablePoints(guest.getGuestId());
        });

        assertEquals("Loyalty points not found for guest", thrown.getMessage());
    }
}