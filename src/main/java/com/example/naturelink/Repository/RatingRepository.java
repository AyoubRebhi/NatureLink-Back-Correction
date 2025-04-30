package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT r FROM Rating r WHERE r.pack.id = :packId")
    // Optionally, you can add methods to fetch ratings by reservation ID
    List<Rating> findByPackId(Long packId);
    @Query("SELECT r FROM Rating r WHERE r.pack.id = :packId AND r.user.id = :userId")
    Rating findByPackIdAndUserId(Long packId, Long userId);
}
