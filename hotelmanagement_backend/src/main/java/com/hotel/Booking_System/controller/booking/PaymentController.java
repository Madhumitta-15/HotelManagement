package com.hotel.Booking_System.controller.booking;


import com.hotel.Booking_System.model.Payment;
import com.hotel.Booking_System.model.enums.PaymentMethod;
import com.hotel.Booking_System.repository.bookRepo.PaymentRepository;
import com.hotel.Booking_System.service.bookServices.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @GetMapping("/allpayments")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentDetails(@PathVariable Integer paymentId) {
        Payment payment = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(payment);
    }
}

