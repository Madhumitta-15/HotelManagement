package com.hotel.Booking_System.exception;

public class HotelAlreadyAssignedException extends RuntimeException{
    public HotelAlreadyAssignedException(String s){
        super(s);
    }
}
