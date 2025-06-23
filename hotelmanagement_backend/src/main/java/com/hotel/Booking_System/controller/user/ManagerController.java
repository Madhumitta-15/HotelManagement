package com.hotel.Booking_System.controller.user;

import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.exception.ManagerNotFoundException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import com.hotel.Booking_System.service.userService.ManagerService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import com.hotel.Booking_System.service.userService.ManagerUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/manager")
//@PreAuthorize("hasRole('ADMIN')") // Secure this controller/endpoint for ADMIN role only
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ManagerUserDetailsService managerUserDetailsService;

    @Autowired
    private ManagerRepository managerRepository;

    //adding manager
    @PostMapping("/addManager")
    public ResponseEntity<String> addHotelManager(@Valid @RequestBody Manager manager){
        try{
            managerService.addHotelManager(manager);
            return ResponseEntity.ok("Hotel Manager added successfully.");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add Hotel Manager: " + e.getMessage());
        }

    }

    //mananger login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateGuest(@RequestBody Map<String, String> authRequest) {
        String username = authRequest.get("username");
        String password = authRequest.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = managerUserDetailsService.loadUserByUsername(username);
                Manager manager = managerRepository. findByManagerName(username)
                        .orElseThrow(() -> new RuntimeException("Manager not found")); // Fetch the Manager entity
                String managerId = String.valueOf(manager.getManagerId());

                String jwtToken = jwtService.generateToken(userDetails,managerId,"MANAGER");

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

    //view manager profile with id
    @GetMapping("/managerprofile/{managerId}")
    public ResponseEntity<?> viewHotelManager(
            @PathVariable int managerId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(token);
        String loggedInManagerId = claims.get("userId", String.class);

        UserRepresentationDTO manager = managerService.viewHotelManager(managerId); // Use the existing getManagerById

        if (loggedInManagerId != null && loggedInManagerId.equals(String.valueOf(manager.getId()))) {
            return ResponseEntity.ok(manager); // Return the entire Manager entity
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. You are not authorized to view this profile.");
        }
    }

    //Deleting Manager with id
    @DeleteMapping("/deleteManager/{managerId}")
    public ResponseEntity<String> deleteManager(@PathVariable int managerId) {
        try {
            managerService.deleteManager(managerId);
            return ResponseEntity.ok("Hotel with ID '" + managerId + "' deleted successfully.");
        } catch (ManagerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // HTTP 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete hotel: " + e.getMessage());
        }
    }
    //update manager with id
    @PatchMapping("/updatemanager/{managerId}")
    public ResponseEntity<String> updateManager(@PathVariable int managerId ,@Valid @RequestBody UserUpdateDTO updatedManager ) {
        try {
            managerService.updateManager(managerId, updatedManager);
            return ResponseEntity.status(HttpStatus.OK).body("Manager profile updated successfully ");
        }catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager with "+managerId+" not found");

        }
    }


}
