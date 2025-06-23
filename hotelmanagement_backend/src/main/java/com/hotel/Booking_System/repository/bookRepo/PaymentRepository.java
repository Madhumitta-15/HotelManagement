package com.hotel.Booking_System.repository.bookRepo;

import com.hotel.Booking_System.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByRequestId(String requestId);
}