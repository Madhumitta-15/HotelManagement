package com.hotel.Booking_System.controller.loyalty;

import com.hotel.Booking_System.service.loyaltyService.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;

@RestController
@RequestMapping("loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private  final JWTServiceImpl jwtServiceImpl;

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalanceFromToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String guestIdStr = jwtServiceImpl.extractClaim(jwt, claims -> claims.get("userId", String.class));
            if (guestIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Integer guestId = Integer.parseInt(guestIdStr);
            int balance = loyaltyService.getAvailablePoints(guestId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
