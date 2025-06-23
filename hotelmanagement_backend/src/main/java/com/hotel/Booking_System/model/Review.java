package com.hotel.Booking_System.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private Integer reviewId;

//    @Column(name = "UserID")
//    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "HotelID")
    @JsonBackReference
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name="guest_id")
    private Guest guest;



    @Column(name="Email")
    private String email;


    @Column(name = "Rating")
    private Integer rating;

    @Column(name = "Comment")
    private String comment;

    @CreationTimestamp
    @Column(name = "Timestamp")
    private Timestamp timestamp;

    private String category;


}