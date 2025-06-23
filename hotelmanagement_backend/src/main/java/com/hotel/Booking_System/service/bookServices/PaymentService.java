// src/main/java/com/hotel/Booking_System/service/bookServices/PaymentService.java
package com.hotel.Booking_System.service.bookServices;

import com.hotel.Booking_System.model.Payment;
import com.hotel.Booking_System.model.enums.PaymentMethod;

public interface PaymentService {
    Payment createPayment(Integer guestId, double amount, PaymentMethod paymentMethod, String requestId); // <-- MODIFIED SIGNATURE
    Payment getPaymentDetails(Integer paymentId);
}