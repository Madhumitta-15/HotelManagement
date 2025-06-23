package com.hotel.Booking_System.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hotel.Booking_System.model.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;


    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer availability;

    private String features;

    @ManyToOne
    @JoinColumn(name = "hotelId", nullable = false)
    @JsonBackReference
    private Hotel hotel;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;

    private String imageUrl;

//    @ManyToOne
//    @JoinColumn(name="guest_id")
//    private Guest guest;
}

