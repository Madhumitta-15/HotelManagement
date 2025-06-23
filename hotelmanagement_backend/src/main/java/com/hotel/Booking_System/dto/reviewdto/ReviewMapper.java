package com.hotel.Booking_System.dto.reviewdto;
import com.hotel.Booking_System.model.Review;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public ReviewDTO toReviewDto(Review review) {

        if (review == null) {
            return null;

        }
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setGuestUserName(review.getGuest().getGuestUserName());
        reviewDto.setComment(review.getComment());
        reviewDto.setRating(review.getRating());

        if(review.getTimestamp() != null) {
            reviewDto.setReviewDate((DATE_FORMAT.format(review.getTimestamp())));
        }

        reviewDto.setHotelId(review.getHotel().getHotelId());
        reviewDto.setCategory(review.getCategory());

        return reviewDto;

    }

    public List<ReviewDTO> toReviewDtoList(List<Review> reviews) {

        if (reviews == null) {
            return null;
        }

        return reviews.stream()
                .map(this::toReviewDto)
                .collect(Collectors.toList());
    }
    public Review toReview(ReviewDTO reviewDto) {

        if (reviewDto == null) {
            return null;
        }

        Review review = new Review();
        review.setReviewId(reviewDto.getReviewId());
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.getHotel().setHotelId(reviewDto.getHotelId());
        review.setCategory(reviewDto.getCategory());
        return review;
    }
}

