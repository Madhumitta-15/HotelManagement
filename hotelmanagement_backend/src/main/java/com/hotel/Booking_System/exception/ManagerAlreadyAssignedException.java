package com.hotel.Booking_System.exception;


public class ManagerAlreadyAssignedException extends RuntimeException {
    public ManagerAlreadyAssignedException(String message) {
        super(message);
    }
}

