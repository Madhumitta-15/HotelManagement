package com.hotel.Booking_System.exception;

public class UserIdFailedException extends RuntimeException {
    public UserIdFailedException(String message) {
        super(message);
    }
}
