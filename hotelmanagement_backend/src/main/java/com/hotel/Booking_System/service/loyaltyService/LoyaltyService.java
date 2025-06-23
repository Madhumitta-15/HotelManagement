package com.hotel.Booking_System.service.loyaltyService;

public interface LoyaltyService {

    void addPoints(Integer guestId, int points);
    double redeemPoints(Integer guestId, double amount);
    int getAvailablePoints(Integer userId);
}
