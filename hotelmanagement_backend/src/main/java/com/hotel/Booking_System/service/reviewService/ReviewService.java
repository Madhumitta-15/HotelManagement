package com.hotel.Booking_System.service.reviewService;

import com.hotel.Booking_System.exception.HotelNotFoundException;

import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.UserIdFailedException;

import com.hotel.Booking_System.exception.UserNotFoundException;

import com.hotel.Booking_System.model.Review;

import java.util.List;

import java.util.Map;

import java.util.Optional;

public interface ReviewService {

//    List<Review> getAllReviews();

    Optional<Review> getReviewById(Integer reviewId);

    Review addReview(Integer hotelId,String token, Review review) throws HotelNotFoundException, UserIdFailedException, UserNotFoundException;

    String deleteReview(Integer id, String token) throws ResourceNotFoundException, UserNotFoundException;
    List<Review> getReviewsByHotelId(Integer hotelId);

    List<Review> getReviewsByHotelName(String hotelName);

    Review updateReview(Integer hotelId,Integer reviewId, String token, Review review) throws HotelNotFoundException, UserIdFailedException, UserNotFoundException;



}

