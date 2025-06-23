package com.hotel.Booking_System.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.util.List;

@Entity

@Data
@Builder
@NoArgsConstructor

@AllArgsConstructor

public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name="hotel_Id")
    private Integer hotelId;

    @Column(nullable = false)
    private String hotelName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = true) // Make it nullable if images are not strictly required for every hotel
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private String amenities;

    @OneToMany(mappedBy = "hotel",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Review> reviews;

    @OneToMany(mappedBy = "hotel")
    @JsonManagedReference
    private List<Room>rooms;

    @OneToMany(mappedBy = "hotelBooked")
    private List<Guest> guests;

    private String description;

}

