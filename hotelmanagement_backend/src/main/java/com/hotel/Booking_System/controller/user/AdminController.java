package com.hotel.Booking_System.controller.user;

import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.userService.AdminService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    private JWTServiceImpl jwtService;

    //admin login

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAdmin(@RequestBody Map<String, String> authRequest) {
        String username = authRequest.get("username");
        String password = authRequest.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(username);
                String adminId = username;

                String jwtToken = jwtService.generateToken(userDetails, adminId, "ADMIN");

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


    @GetMapping("/viewallguests")
    public List<GuestDTO> viewAllGuests(){
        return adminService.viewAllGuests();
    }

    @GetMapping("/viewallmanagers")
    public List<Manager> viewAllManagers(){
        return adminService.viewAllManagers();
    }




}

