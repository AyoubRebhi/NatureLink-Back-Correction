package com.example.naturelink.Service;

import com.example.naturelink.Entity.Pack;
import com.example.naturelink.Entity.Rating;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Repository.PackRepository;
import com.example.naturelink.Repository.RatingRepository;
import com.example.naturelink.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private UserRepository userRepository;

    public Rating addOrUpdateRating(Long packId, int ratingValue, Long userId) {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }

        // Fetch User entity
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check for existing rating
        Rating existingRating = ratingRepository.findByPackIdAndUserId(packId, userId);
        if (existingRating != null) {
            existingRating.setRating(ratingValue);
            return ratingRepository.save(existingRating);
        }

        // Fetch Pack entity
        Pack pack = packRepository.findById(packId)
                .orElseThrow(() -> new RuntimeException("Pack not found with id: " + packId));

        // Create new rating
        Rating rating = new Rating();
        rating.setRating(ratingValue);
        rating.setPack(pack);
        rating.setUser(user); // Set User entity instead of userId
        return ratingRepository.save(rating);
    }

    public double getAverageRatingForPack(Long packId) {
        List<Rating> ratings = ratingRepository.findByPackId(packId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }
}