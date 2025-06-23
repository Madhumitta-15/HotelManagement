package com.hotel.Booking_System.controller.user;

import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.userService.GuestService;
import com.hotel.Booking_System.service.userService.GuestUserDetailsService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import io.jsonwebtoken.Claims; // Import Claims for extracting data from the token
import jakarta.el.BeanELResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.jsonwebtoken.Jwts.claims;

@RestController
@RequestMapping("/guest")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @Autowired
    private JWTServiceImpl jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GuestUserDetailsService guestUserDetailsService;

    @Autowired
    private GuestRepository guestRepository;


    //Guest registration
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Guest guest) {
        try {
            guestService.register(guest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Registration failed: " + e.getMessage());
        }
    }

    //guest login

    @PostMapping("/login")
    public ResponseEntity<?> authenticateGuest(@RequestBody Map<String, String> authRequest) {
        String username = authRequest.get("username");
        String password = authRequest.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = guestUserDetailsService.loadUserByUsername(username);
                Guest guest = guestRepository.findByGuestUserName(username)
                        .orElseThrow(() -> new RuntimeException("Guest not found")); // Fetch the Guest entity
                String guestId = String.valueOf(guest.getGuestId());
                String jwtToken = jwtService.generateToken(userDetails,guestId,"GUEST");

                return ResponseEntity.ok(jwtToken);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(errorResponse);
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication failed");
        return ResponseEntity.status(401).body(errorResponse);
    }

    //guest profile
    @GetMapping("/viewguest/{guestname}")
    public ResponseEntity<?> guestProfile(
            @PathVariable String guestname,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(token);
        String loggedInGuestId = claims.get("userId", String.class);

        GuestDTO guestProfile = guestService.guestProfile(guestname);

        if (loggedInGuestId != null && loggedInGuestId.equals(String.valueOf(guestProfile.getGuestId()))) {
            return ResponseEntity.ok(guestProfile);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. You are not authorized to view this profile.");
        }
    }

    ///update guest
    @PatchMapping("/updateguest/{guestname}")
    public ResponseEntity<String> updateGuest(
            @PathVariable String guestname,
            @RequestBody UserUpdateDTO newGuest,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(token);
        String loggedInGuestId = claims.get("userId", String.class);

        UserRepresentationDTO existingGuest = guestService.viewGuest(guestname);

        if (loggedInGuestId != null && loggedInGuestId.equals(String.valueOf(existingGuest.getId()))) {
            guestService.updateGuest(guestname, newGuest);
            return ResponseEntity.status(HttpStatus.OK).body("guest updated");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. You are not authorized to update this profile.");
        }
    }

    //update guest using guest id
    @PatchMapping("/updateprofile")
    public ResponseEntity<String> updateGuestProfile(

            @RequestBody UserUpdateDTO newGuest,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = authorizationHeader.substring(7);

            String guestIdFromToken = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
            if (guestIdFromToken == null) {
                throw new RuntimeException("User information not found in the token.");
            }
            Integer guestId = Integer.parseInt(guestIdFromToken);
            if (guestId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token: User ID claim missing or not an Integer type.");
            }
            guestService.updateGuestById(guestId, newGuest);
            return ResponseEntity.status(HttpStatus.OK).body("Guest profile updated successfully.");
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid user ID type in token claims. Expected Integer. Error: " + e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token expired. Please log in again.");
        } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token. Please log in again. Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile due to an unexpected error.");
        }
    }

    //deleting guest
    @DeleteMapping("/deleteGuest/{guestName}")
    public ResponseEntity<String> deleteGuest(
            @PathVariable String guestName,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(token);
        String loggedInGuestId = claims.get("userId", String.class);

        UserRepresentationDTO guestToDelete = guestService.viewGuest(guestName);

        if (loggedInGuestId != null && loggedInGuestId.equals(String.valueOf(guestToDelete.getId()))) {
            try {
                guestService.deleteGuest(guestName);
                return ResponseEntity.ok("Guest with name '" + guestName + "' deleted successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete guest: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. You are not authorized to delete this profile.");
        }
    }




}