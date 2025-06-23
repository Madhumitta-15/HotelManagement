package com.hotel.Booking_System.exception;

public class HotelAlreadyExistsException extends RuntimeException{
    public HotelAlreadyExistsException(String s){
        super(s);
    }
}
