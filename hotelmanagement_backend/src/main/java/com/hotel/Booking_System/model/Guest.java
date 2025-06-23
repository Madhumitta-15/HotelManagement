package com.hotel.Booking_System.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hotel.Booking_System.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guestId;
    private String guestName;
    private String guestUserName;
    private String guestPassword;
    private String guestEmail;
    private String guestContact;


    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "hotel_id") // Foreign key to Hotel entity
    private Hotel hotelBooked; // Many-to-One with Hotel

    @OneToMany(mappedBy = "guest")
    private List<Booking> bookings; // One-to-Many with Booking

    @OneToMany(mappedBy = "guest")
    private List<Payment> payments; // One-to-Many with Payment

    @OneToMany(mappedBy = "guest")
    private List<Review> reviews; // One-to-Many with Review

    @OneToOne(mappedBy = "guest")
    private LoyaltyPoints loyaltyPoints; // One-to-One with LoyaltyPoints



}
