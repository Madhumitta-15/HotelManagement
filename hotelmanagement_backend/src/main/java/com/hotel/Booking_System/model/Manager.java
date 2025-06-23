package com.hotel.Booking_System.model;

import com.hotel.Booking_System.model.enums.Role;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Manager {

    @Id
    @GeneratedValue
    private Integer managerId;

    @NotNull(message = "name should not be empty")
    private String managerName;

    private String managerPassword;
    @NotNull(message = "email should not be empty")
    private String managerEmail;
    @NotNull(message = "contact should not be empty")
    @Pattern(regexp = "0?([1-9]\\d{9})$",message = "contact number should contain 10 digits")
    private String managerContact;

    @Enumerated(EnumType.STRING)
    private Role role;

//    public enum Role {
//        MANAGER
//    }

    @OneToOne(mappedBy = "manager")
    @JsonBackReference
    private Hotel hotel;
}