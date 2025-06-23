package com.hotel.Booking_System.controller.review;
import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.dto.reviewdto.ReviewMapper;
import com.hotel.Booking_System.exception.HotelNotFoundException;
import com.hotel.Booking_System.exception.UserIdFailedException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.service.reviewService.EmailService;
import com.hotel.Booking_System.service.reviewService.ReviewFilterService;
import com.hotel.Booking_System.service.reviewService.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ReviewFilterService reviewFilterService;

    //success
    @GetMapping("/filter/{hotelId}")
    public ResponseEntity<Map<String, Object>> getReviewsByHotelId(
            @PathVariable Integer hotelId,
            @RequestParam(required = false) String sortBy) {


        List<Review> allReviews = reviewService.getReviewsByHotelId(hotelId);
        List<Review> filteredReviews = reviewFilterService.filterAndSortReviews(allReviews, sortBy);
        Map<String, Object> result = reviewFilterService.getReviewDetails(filteredReviews);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    //success
    @GetMapping("/{hotelId}")
    public ResponseEntity<Map<String,Object>> getReviewsByHotelId(@PathVariable Integer hotelId) {

        List<Review> reviews = reviewService.getReviewsByHotelId(hotelId);
        List<ReviewDTO> reviewDTOS = reviewMapper.toReviewDtoList(reviews);
        Map<String, Object> ReviewByID= reviewFilterService.getHotelReviewSummary(hotelId);
        ReviewByID.put("reviews", reviewDTOS);

        return new ResponseEntity<>(ReviewByID, HttpStatus.OK);

    }

    //success
    @GetMapping("/getbycategory/{hotelId}/{category}")

    public List<ReviewDTO> getByCategory(@PathVariable Integer hotelId, @PathVariable String category){
        return  reviewFilterService.getReviewByCategory(hotelId,category);

    }

    //success
    @GetMapping("/hotelname/{hotelName}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByHotelName(@PathVariable String hotelName) {
        List<Review> reviews = reviewService.getReviewsByHotelName(hotelName);
        List<ReviewDTO> reviewDTOS = reviewMapper.toReviewDtoList(reviews);

        if (reviewDTOS.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reviewDTOS, HttpStatus.OK);

    }

    //success
    @GetMapping("/{hotelId}/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Integer hotelId, @PathVariable Integer reviewId) {

        Optional<Review> review = reviewService.getReviewById(reviewId);
        return review.map(r -> new ResponseEntity<>(reviewMapper.toReviewDto(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    //success
    @PostMapping("/add/{hotelId}")
    public ResponseEntity<?> addReview(@PathVariable Integer hotelId, @RequestBody Review review,@RequestHeader("Authorization") String authorizationHeader)throws UserNotFoundException {

        try {

            String token = authorizationHeader.substring(7);
            Review savedReview = reviewService.addReview(hotelId, token,review);
            ReviewDTO savedReviewDTO = reviewMapper.toReviewDto(savedReview);
            if (review.getEmail() != null && !review.getEmail().isEmpty()) {
                emailService.sendEmail(review.getEmail(), "Review Confirmation", "Thank you for your review!");

            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Review added Successfully.");
        }catch (UserIdFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed add review to this hotel");
        }catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("hotel not found");
        }catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");

        }

    }

    //success
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteReview( @PathVariable Integer id,@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        reviewService.deleteReview(id,token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


    //completed
    @PutMapping("/edit/{hotelId}/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Integer hotelId, @PathVariable Integer reviewId,@RequestBody Review review,@RequestHeader("Authorization") String authorizationHeader) throws UserNotFoundException {

        try {
            String token = authorizationHeader.substring(7);
            Review updatedReview = reviewService.updateReview(hotelId,reviewId, token, review);

            ReviewDTO updatedReviewDTO = reviewMapper.toReviewDto(updatedReview);

            return ResponseEntity.ok(updatedReviewDTO);
        } catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (UserIdFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        }

    }

    //success
    @GetMapping("/summary/{hotelId}")

    public ResponseEntity<Map<String, Object>> getHotelReviewSummary(@PathVariable Integer hotelId) {
        Map<String, Object> summary = reviewFilterService.getHotelReviewSummary(hotelId);
        return new ResponseEntity<>(summary, HttpStatus.OK);

    }

}
