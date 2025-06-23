package com.hotel.Booking_System.service.reviewService;

public interface EmailService {
  String sendEmail(String toEmail, String subject, String body);
}
