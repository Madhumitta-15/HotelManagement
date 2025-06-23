package com.hotel.Booking_System.service.reviewService;

import com.hotel.Booking_System.exception.HotelNotFoundException;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.UserIdFailedException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.repository.bookRepo.BookingRepository;
import com.hotel.Booking_System.repository.reviewRepo.ReviewRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.hotelServices.HotelService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import org.eclipse.angus.mail.imap.protocol.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hotel.Booking_System.model.enums.BookingStatus; // <--- This import is crucial for BookingStatus


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements  ReviewService{

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Optional<Review> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public Review addReview(Integer hotelId, String token,Review review) throws HotelNotFoundException, UserIdFailedException, UserNotFoundException {
        Optional<Hotel> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException("Hotel with ID " + hotelId + " not found.");
        }
        Hotel hotel = hotelOptional.get();
        String guestIdFromToken = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        if (guestIdFromToken == null) {
            throw new RuntimeException("User information not found in the token.");
        }
        Integer guestId = Integer.parseInt(guestIdFromToken);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new UserNotFoundException("Guest not found with ID: " + guestId)); // Corrected exception

        List<Booking> successfulBookings = bookingRepository.findByGuest_GuestIdAndStatus(guestId, BookingStatus.CONFIRMED);

        if (successfulBookings.isEmpty()) {
            throw new UserIdFailedException("User with ID " + guestId + " does not have any successful booking and cannot add a review.");
        }
        boolean hasBookedThisHotel = successfulBookings.stream()
                .anyMatch(booking -> booking.getRoom().getHotel().getHotelId().equals(hotelId));

        if (!hasBookedThisHotel) {
            throw new UserIdFailedException("User with ID " + guestId + " has not booked hotel with ID " + hotelId + " and cannot add a review.");
        }

        review.setHotel(hotel);
        review.setGuest(guest);
        review.setEmail(guest.getGuestEmail());
        Review savedReview = reviewRepository.save(review);
        System.out.println(review.getEmail());

        return savedReview;
    }

    @Override
    public String deleteReview(Integer id, String token) throws ResourceNotFoundException, UserNotFoundException {
    Optional<Review> reviewOptional = reviewRepository.findById(id);
    if (reviewOptional.isEmpty()) {
        throw new ResourceNotFoundException("Review with ID " + id + " not found.");
    }
    Review reviewToDelete = reviewOptional.get();

    String guestIdFromToken = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
    if (guestIdFromToken == null) {
        throw new UserNotFoundException("Invalid or malformed token: User ID not found.");
    }
    Integer loggedInGuestId = Integer.parseInt(guestIdFromToken);
    Integer reviewCreatorGuestId = reviewToDelete.getGuest().getGuestId();

    if (!loggedInGuestId.equals(reviewCreatorGuestId)) {
        throw new UserIdFailedException("You are not authorized to delete this review. Only the creator can delete it.");
    }
    reviewRepository.deleteById(id);
    return "Review Deleted Successfully";
}

    @Override
    public List<Review> getReviewsByHotelId(Integer hotelId) {
        return reviewRepository.findByHotel_HotelId(hotelId);
    }

    @Override
    public List<Review> getReviewsByHotelName(String hotelName) {
        return reviewRepository.findByHotel_HotelName(hotelName);
    }

    @Override
    public Review updateReview(Integer hotelId,Integer reviewId, String token, Review review) throws HotelNotFoundException, UserIdFailedException, UserNotFoundException { // Corrected exception
        Optional<Hotel> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new HotelNotFoundException("Hotel with ID " + hotelId + " not found.");
        }
        Optional<Review> existingReviewOptional=reviewRepository.findById(reviewId);
        if(existingReviewOptional.isEmpty()){
            throw new HotelNotFoundException("Review with ID "+reviewId+" not found");
        }

        Review existingReview = existingReviewOptional.get();
        String guestIdFromToken = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        if (guestIdFromToken == null) {
            throw new RuntimeException("User information not found in the token.");
        }
        Integer guestId = Integer.parseInt(guestIdFromToken);
        if(!existingReview.getGuest().getGuestId().equals(guestId)){
            throw new UserIdFailedException("User with ID " + guestId + " is not authorized to edit this review.");
        }

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new UserNotFoundException("Guest not found with ID: " + guestId));

        List<Booking> successfulBookings = bookingRepository.findByGuest_GuestIdAndStatus(
                guestId,
                BookingStatus.CONFIRMED
        );

        if (successfulBookings.isEmpty()) {
            throw new UserIdFailedException("User with ID " + guestId + " does not have a successful booking and cannot update the review.");
        }

        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());
        existingReview.setHotel(hotelOptional.get());
        existingReview.setEmail(guest.getGuestEmail());

        return reviewRepository.save(existingReview);
    }


}