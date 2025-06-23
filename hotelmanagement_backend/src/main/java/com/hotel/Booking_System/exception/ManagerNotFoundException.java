package com.hotel.Booking_System.exception;


public class ManagerNotFoundException extends RuntimeException {
    public ManagerNotFoundException(String message){
        super(message);
    }
}

