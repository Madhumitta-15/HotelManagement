package com.hotel.Booking_System.serviceTest;

import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.exception.HotelNotFoundException;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.UserIdFailedException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.model.Room;
import com.hotel.Booking_System.model.enums.BookingStatus;
import com.hotel.Booking_System.repository.bookRepo.BookingRepository;
import com.hotel.Booking_System.repository.reviewRepo.ReviewRepository;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.hotelServices.HotelService;
import com.hotel.Booking_System.service.reviewService.EmailServiceImpl;
import com.hotel.Booking_System.service.reviewService.ReviewFilterService;
import com.hotel.Booking_System.service.reviewService.ReviewServiceImpl;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServicesTest {

    @Mock
    private JavaMailSender mailSender; // For EmailServiceImpl

    @Mock
    private ReviewRepository reviewRepository; // For ReviewFilterService and ReviewServiceImpl
    @Mock
    private HotelService hotelService; // For ReviewServiceImpl
    @Mock
    private JWTServiceImpl jwtService; // For ReviewServiceImpl
    @Mock
    private GuestRepository guestRepository; // For ReviewServiceImpl
    @Mock
    private BookingRepository bookingRepository; // For ReviewServiceImpl

    @InjectMocks
    private EmailServiceImpl emailService;

    @InjectMocks
    private ReviewFilterService reviewFilterService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Hotel testHotel;
    private Guest testGuest;
    private Review testReview;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testHotel = new Hotel();
        testHotel.setHotelId(1);
        testHotel.setHotelName("Test Hotel");

        testGuest = new Guest();
        testGuest.setGuestId(101);
        testGuest.setGuestUserName("testguest");
        testGuest.setGuestEmail("test@example.com");

        testReview = new Review();
        testReview.setReviewId(1);
        testReview.setHotel(testHotel);
        testReview.setGuest(testGuest);
        testReview.setRating(5);
        testReview.setComment("Great stay!");
        testReview.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        testReview.setEmail(testGuest.getGuestEmail());
        testReview.setCategory("Service");

        Room room = new Room();
        room.setRoomId(1001);
        room.setHotel(testHotel);

        testBooking = new Booking();
        testBooking.setBookingId(201);
        testBooking.setGuest(testGuest);
        testBooking.setRoom(room);
        testBooking.setStatus(BookingStatus.CONFIRMED);
    }

    // EmailServiceImpl Tests
    @Test
    void sendEmail_success() {
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        String result = emailService.sendEmail(toEmail, subject, body);

        assertEquals("Email sent successfully!", result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_failure() {
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("Mail send error")).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendEmail(toEmail, subject, body);

        assertEquals("Email sending error!", result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ReviewFilterService Tests
    @Test
    void filterAndSortReviews_recent() {
        Review review1 = new Review();
        review1.setReviewId(1);
        review1.setTimestamp(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        review1.setRating(4);

        Review review2 = new Review();
        review2.setReviewId(2);
        review2.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        review2.setRating(5);

        List<Review> reviews = new ArrayList<>(Arrays.asList(review1, review2));
        List<Review> sortedReviews = reviewFilterService.filterAndSortReviews(reviews, "recent");

        assertEquals(2, sortedReviews.get(0).getReviewId());
        assertEquals(1, sortedReviews.get(1).getReviewId());
    }

    @Test
    void filterAndSortReviews_lowToHigh() {
        Review review1 = new Review();
        review1.setReviewId(1);
        review1.setRating(4);

        Review review2 = new Review();
        review2.setReviewId(2);
        review2.setRating(2);

        List<Review> reviews = new ArrayList<>(Arrays.asList(review1, review2));
        List<Review> sortedReviews = reviewFilterService.filterAndSortReviews(reviews, "lowtohigh");

        assertEquals(2, sortedReviews.get(0).getReviewId());
        assertEquals(1, sortedReviews.get(1).getReviewId());
    }

    @Test
    void filterAndSortReviews_highToLow() {
        Review review1 = new Review();
        review1.setReviewId(1);
        review1.setRating(4);

        Review review2 = new Review();
        review2.setReviewId(2);
        review2.setRating(2);

        List<Review> reviews = new ArrayList<>(Arrays.asList(review1, review2));
        List<Review> sortedReviews = reviewFilterService.filterAndSortReviews(reviews, "hightolow");

        assertEquals(1, sortedReviews.get(0).getReviewId());
        assertEquals(2, sortedReviews.get(1).getReviewId());
    }

    @Test
    void getReviewByCategory_success() {
        List<Review> allReviews = new ArrayList<>();
        allReviews.add(testReview); // Category "Service"
        Review otherCategoryReview = new Review();
        otherCategoryReview.setReviewId(2);
        otherCategoryReview.setCategory("Location");
        otherCategoryReview.setHotel(testHotel);
        allReviews.add(otherCategoryReview);

        when(reviewRepository.findAll()).thenReturn(allReviews);

        List<ReviewDTO> result = reviewFilterService.getReviewByCategory(testHotel.getHotelId(), "Service");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReview.getReviewId(), result.get(0).getReviewId());
        assertEquals("Service", result.get(0).getCategory());
    }

    @Test
    void getHotelReviewSummary_noReviews() {
        when(reviewRepository.findByHotel_HotelId(testHotel.getHotelId())).thenReturn(Collections.emptyList());

        Map<String, Object> summary = reviewFilterService.getHotelReviewSummary(testHotel.getHotelId());

        assertNotNull(summary);
        assertEquals(0.0, summary.get("overallRating"));
        assertEquals(0, summary.get("totalReviews"));
        assertEquals("No ratings yet", summary.get("reviewDescription"));
        assertTrue(((Map<String, Double>) summary.get("ratingPercentages")).isEmpty());
    }

    @Test
    void getHotelReviewSummary_withReviews() {
        Review review1 = new Review();
        review1.setRating(5);
        Review review2 = new Review();
        review2.setRating(4);
        Review review3 = new Review();
        review3.setRating(5);

        List<Review> reviews = Arrays.asList(review1, review2, review3);
        when(reviewRepository.findByHotel_HotelId(testHotel.getHotelId())).thenReturn(reviews);

        Map<String, Object> summary = reviewFilterService.getHotelReviewSummary(testHotel.getHotelId());

        assertNotNull(summary);
        assertEquals(4.666666666666667, (Double) summary.get("overallRating"), 0.001); // Using delta for double comparison
        assertEquals(3, summary.get("totalReviews"));
        assertEquals("Excellent", summary.get("reviewDescription"));
        Map<String, Double> percentages = (Map<String, Double>) summary.get("ratingPercentages");
        assertEquals(0.0, percentages.get("1"));
        assertEquals(0.0, percentages.get("2"));
        assertEquals(0.0, percentages.get("3"));
        assertEquals(33.33333333333333, percentages.get("4"), 0.001);
        assertEquals(66.66666666666666, percentages.get("5"), 0.001);
    }

    @Test
    void getReviewDetails_success() {
        List<Review> reviews = Collections.singletonList(testReview);

        Map<String, Object> result = reviewFilterService.getReviewDetails(reviews);

        assertNotNull(result);
        assertTrue(result.containsKey("reviews"));
        List<ReviewDTO> reviewDTOS = (List<ReviewDTO>) result.get("reviews");
        assertEquals(1, reviewDTOS.size());
        assertEquals(testReview.getReviewId(), reviewDTOS.get(0).getReviewId());
        assertEquals(testReview.getRating(), reviewDTOS.get(0).getRating());
        assertEquals(testReview.getComment(), reviewDTOS.get(0).getComment());
    }


    // ReviewServiceImpl Tests
    @Test
    void getReviewById_found() {
        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));

        Optional<Review> result = reviewService.getReviewById(testReview.getReviewId());

        assertTrue(result.isPresent());
        assertEquals(testReview.getReviewId(), result.get().getReviewId());
    }

    @Test
    void getReviewById_notFound() {
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.getReviewById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void addReview_success() throws HotelNotFoundException, UserIdFailedException, UserNotFoundException {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(testGuest.getGuestId())).thenReturn(Optional.of(testGuest));
        when(bookingRepository.findByGuest_GuestIdAndStatus(testGuest.getGuestId(), BookingStatus.CONFIRMED))
                .thenReturn(Collections.singletonList(testBooking));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review newReview = new Review();
        newReview.setRating(5);
        newReview.setComment("Excellent service.");
        newReview.setCategory("Service");

        Review savedReview = reviewService.addReview(testHotel.getHotelId(), token, newReview);

        assertNotNull(savedReview);
        assertEquals(testReview.getReviewId(), savedReview.getReviewId());
        assertEquals(testHotel.getHotelId(), savedReview.getHotel().getHotelId());
        assertEquals(testGuest.getGuestId(), savedReview.getGuest().getGuestId());
        assertEquals(testGuest.getGuestEmail(), savedReview.getEmail());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_hotelNotFound() {
        String token = "dummyToken";
        Review newReview = new Review();
        when(hotelService.getHotelById(anyInt())).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () -> reviewService.addReview(999, token, newReview));
    }

    @Test
    void addReview_userNotFoundInToken() {
        String token = "dummyToken";
        Review newReview = new Review();
        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> reviewService.addReview(testHotel.getHotelId(), token, newReview));
    }

    @Test
    void addReview_guestNotFound() {
        String token = "dummyToken";
        String guestIdFromToken = "101";
        Review newReview = new Review();
        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(testHotel.getHotelId(), token, newReview));
    }

    @Test
    void addReview_noSuccessfulBookings() {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());
        Review newReview = new Review();

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(testGuest.getGuestId())).thenReturn(Optional.of(testGuest));
        when(bookingRepository.findByGuest_GuestIdAndStatus(testGuest.getGuestId(), BookingStatus.CONFIRMED))
                .thenReturn(Collections.emptyList());

        assertThrows(UserIdFailedException.class, () -> reviewService.addReview(testHotel.getHotelId(), token, newReview));
    }

    @Test
    void addReview_notBookedThisHotel() {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());
        Review newReview = new Review();

        Hotel otherHotel = new Hotel();
        otherHotel.setHotelId(99);
        Room otherRoom = new Room();
        otherRoom.setHotel(otherHotel);
        Booking otherBooking = new Booking();
        otherBooking.setGuest(testGuest);
        otherBooking.setRoom(otherRoom);
        otherBooking.setStatus(BookingStatus.CONFIRMED);

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(testGuest.getGuestId())).thenReturn(Optional.of(testGuest));
        when(bookingRepository.findByGuest_GuestIdAndStatus(testGuest.getGuestId(), BookingStatus.CONFIRMED))
                .thenReturn(Collections.singletonList(otherBooking));

        assertThrows(UserIdFailedException.class, () -> reviewService.addReview(testHotel.getHotelId(), token, newReview));
    }

    @Test
    void deleteReview_success() throws ResourceNotFoundException, UserNotFoundException {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());

        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        doNothing().when(reviewRepository).deleteById(testReview.getReviewId());

        String result = reviewService.deleteReview(testReview.getReviewId(), token);

        assertEquals("Review Deleted Successfully", result);
        verify(reviewRepository, times(1)).deleteById(testReview.getReviewId());
    }

    @Test
    void deleteReview_notFound() {
        String token = "dummyToken";
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(999, token));
    }

    @Test
    void deleteReview_invalidToken_noUserId() {
        String token = "dummyToken";
        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> reviewService.deleteReview(testReview.getReviewId(), token));
    }

    @Test
    void deleteReview_unauthorized() {
        String token = "dummyToken";
        String anotherGuestId = "999"; // Different guest ID

        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(anotherGuestId);

        assertThrows(UserIdFailedException.class, () -> reviewService.deleteReview(testReview.getReviewId(), token));
    }

    @Test
    void getReviewsByHotelId_success() {
        List<Review> reviews = Collections.singletonList(testReview);
        when(reviewRepository.findByHotel_HotelId(testHotel.getHotelId())).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsByHotelId(testHotel.getHotelId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReview.getReviewId(), result.get(0).getReviewId());
    }

    @Test
    void getReviewsByHotelName_success() {
        List<Review> reviews = Collections.singletonList(testReview);
        when(reviewRepository.findByHotel_HotelName(testHotel.getHotelName())).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsByHotelName(testHotel.getHotelName());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReview.getReviewId(), result.get(0).getReviewId());
    }

    @Test
    void updateReview_success() throws HotelNotFoundException, UserIdFailedException, UserNotFoundException {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());

        Review updatedReviewDetails = new Review();
        updatedReviewDetails.setRating(4);
        updatedReviewDetails.setComment("Good stay, but could be better.");
        updatedReviewDetails.setCategory("Amenities");

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(testGuest.getGuestId())).thenReturn(Optional.of(testGuest));
        when(bookingRepository.findByGuest_GuestIdAndStatus(testGuest.getGuestId(), BookingStatus.CONFIRMED))
                .thenReturn(Collections.singletonList(testBooking));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview); // Mock save to return the updated review

        Review result = reviewService.updateReview(testHotel.getHotelId(), testReview.getReviewId(), token, updatedReviewDetails);

        assertNotNull(result);
        assertEquals(updatedReviewDetails.getRating(), result.getRating());
        assertEquals(updatedReviewDetails.getComment(), result.getComment());
        assertEquals(testHotel.getHotelId(), result.getHotel().getHotelId());
        assertEquals(testGuest.getGuestEmail(), result.getEmail());
        verify(reviewRepository, times(1)).save(testReview); // Verify that save was called with the modified existingReview
    }

    @Test
    void updateReview_hotelNotFound() {
        String token = "dummyToken";
        Review updatedReviewDetails = new Review();
        when(hotelService.getHotelById(anyInt())).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () -> reviewService.updateReview(999, testReview.getReviewId(), token, updatedReviewDetails));
    }

    @Test
    void updateReview_reviewNotFound() {
        String token = "dummyToken";
        Review updatedReviewDetails = new Review();
        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () -> reviewService.updateReview(testHotel.getHotelId(), 999, token, updatedReviewDetails));
    }

    @Test
    void updateReview_unauthorizedUser() {
        String token = "dummyToken";
        String anotherGuestId = "999";
        Review updatedReviewDetails = new Review();

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(anotherGuestId);

        assertThrows(UserIdFailedException.class, () -> reviewService.updateReview(testHotel.getHotelId(), testReview.getReviewId(), token, updatedReviewDetails));
    }

    @Test
    void updateReview_userNotFound() {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());
        Review updatedReviewDetails = new Review();

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(testGuest.getGuestId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.updateReview(testHotel.getHotelId(), testReview.getReviewId(), token, updatedReviewDetails));
    }

    @Test
    void updateReview_noSuccessfulBookingForUser() {
        String token = "dummyToken";
        String guestIdFromToken = String.valueOf(testGuest.getGuestId());
        Review updatedReviewDetails = new Review();

        when(hotelService.getHotelById(testHotel.getHotelId())).thenReturn(Optional.of(testHotel));
        when(reviewRepository.findById(testReview.getReviewId())).thenReturn(Optional.of(testReview));
        when(jwtService.extractClaim(eq(token), any())).thenReturn(guestIdFromToken);
        when(guestRepository.findById(testGuest.getGuestId())).thenReturn(Optional.of(testGuest));
        when(bookingRepository.findByGuest_GuestIdAndStatus(testGuest.getGuestId(), BookingStatus.CONFIRMED))
                .thenReturn(Collections.emptyList());

        assertThrows(UserIdFailedException.class, () -> reviewService.updateReview(testHotel.getHotelId(), testReview.getReviewId(), token, updatedReviewDetails));
    }
}
