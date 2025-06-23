package com.hotel.Booking_System.dto.userdto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation to automatically generate getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields
public class ManagerDTO {
    private Integer managerId;
    private String managerName;
    // Add other fields if your frontend needs them for the manager (e.g., email, contact)
    // private String managerEmail;
    // private String managerContact;
}
