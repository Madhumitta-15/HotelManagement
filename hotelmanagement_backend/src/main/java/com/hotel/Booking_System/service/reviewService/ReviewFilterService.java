package com.hotel.Booking_System.service.reviewService;
import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.repository.reviewRepo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewFilterService {

    @Autowired
    private ReviewRepository reviewRepository;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<Review> filterAndSortReviews(List<Review> reviews, String sortBy) {
        if ("recent".equalsIgnoreCase(sortBy)) {
            reviews = reviews.stream()
                    .sorted(Comparator.comparing(Review::getTimestamp).reversed())
                    .collect(Collectors.toList());
        } else if ("lowtohigh".equalsIgnoreCase(sortBy)) {
            reviews = reviews.stream()
                    .sorted(Comparator.comparing(Review::getRating))
                    .collect(Collectors.toList());
        } else if ("hightolow".equalsIgnoreCase(sortBy)) {
            reviews = reviews.stream()
                    .sorted(Comparator.comparing(Review::getRating).reversed())
                    .collect(Collectors.toList());
        }
        return reviews;
    }


    public List<ReviewDTO> getReviewByCategory(Integer hotelId, String category){
        List<Review> allReviews = reviewRepository.findAll();
        List<ReviewDTO> reviewList = new ArrayList<>();
        for(Review r : allReviews){
            if(r.getCategory() != null && r.getCategory().equals(category) && r.getHotel().getHotelId().equals(hotelId)){
                ReviewDTO rd = new ReviewDTO();
                rd.setGuestUserName(r.getGuest().getGuestUserName());
                rd.setReviewId(r.getReviewId());
                rd.setComment(r.getComment());
                rd.setReviewDate(String.valueOf(r.getTimestamp()));
                rd.setRating(r.getRating());
                rd.setHotelId(r.getHotel().getHotelId());
                rd.setCategory(r.getCategory());
                reviewList.add(rd);
            }
        }
        return reviewList;
    }


    public Map<String, Object> getHotelReviewSummary(Integer hotelId) {
        List<Review> reviews = reviewRepository.findByHotel_HotelId(hotelId);
        Map<String, Object> summary = new HashMap<>();
        if (reviews.isEmpty()) {
            summary.put("overallRating", 0.0);
            summary.put("totalReviews", 0);
            summary.put("reviewDescription", "No ratings yet");
            summary.put("ratingPercentages", new HashMap<String, Double>());
            return summary;
        }
        double totalRating = 0;
        Map<Integer, Long> ratingCounts = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        double overallRating = totalRating / reviews.size();

        summary.put("overallRating", overallRating);
        summary.put("totalReviews", reviews.size());
        summary.put("reviewDescription", getRatingDescription(overallRating));

        Map<String, Double> ratingPercentages = new HashMap<>();
        long totalReviewsLong = reviews.size();

        for (int i = 1; i <= 5; i++) {
            long count = ratingCounts.getOrDefault(i, 0L);
            double percentage = (totalReviewsLong > 0) ? ((double) count * 100.0 / totalReviewsLong) : 0.0;
            ratingPercentages.put(String.valueOf(i), percentage);
        }
        summary.put("ratingPercentages", ratingPercentages);

        return summary;
    }

    public Map<String, Object> getReviewDetails(List<Review> reviews) {

        Map<String, Object> result = new HashMap<>();
        double totalRating = 0;

        for (Review review : reviews) {
            totalRating += review.getRating();

        }
        double overallRating = reviews.isEmpty() ? 0 : totalRating / reviews.size();
        int totalReviews = reviews.size();
        String ratingDescription = getRatingDescription(overallRating);

        List<ReviewDTO> reviewDTOS = reviews.stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setReviewId(review.getReviewId());
            dto.setComment(review.getComment());
            dto.setRating(review.getRating());
            dto.setReviewDate(DATE_FORMAT.format(review.getTimestamp()));
            dto.setHotelId(review.getHotel().getHotelId());
            dto.setCategory(review.getCategory());
            return dto;

        }).collect(Collectors.toList());
        result.put("reviews", reviewDTOS);
        return result;
    }


    private String getRatingDescription(double rating) {

        if (rating >= 4.5) {
            return "Excellent";

        } else if (rating >= 4.0) {
            return "Good";

        } else if (rating >= 3.0) {
            return "Average";

        } else {
            return "Poor";

        }
    }


}

