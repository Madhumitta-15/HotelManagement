package com.hotel.Booking_System.service.bookServices;

import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.Payment;
import com.hotel.Booking_System.model.enums.PaymentMethod;
import com.hotel.Booking_System.model.enums.PaymentStatus;
import com.hotel.Booking_System.repository.bookRepo.PaymentRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Import Transactional

import java.util.Optional; // <-- Import Optional
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final GuestRepository guestRepository;

    @Override
    @Transactional
    public Payment createPayment(Integer guestId, double amount, PaymentMethod paymentMethod, String requestId) {
        Optional<Payment> existingPayment = paymentRepository.findByRequestId(requestId);
        if (existingPayment.isPresent()) {
            System.out.println("Payment request with requestId: " + requestId + " already processed. Returning existing payment.");
            return existingPayment.get();
        }

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        PaymentStatus simulatedStatus = new Random().nextBoolean() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .guest(guest)
                .amount(amount)
                .method(paymentMethod)
                .status(simulatedStatus)
                .requestId(requestId)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentDetails(Integer paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}