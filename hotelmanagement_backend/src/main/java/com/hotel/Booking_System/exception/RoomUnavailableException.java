package com.hotel.Booking_System.exception;

public class RoomUnavailableException extends RuntimeException {
  public RoomUnavailableException(String message) {
    super(message);
  }
}
