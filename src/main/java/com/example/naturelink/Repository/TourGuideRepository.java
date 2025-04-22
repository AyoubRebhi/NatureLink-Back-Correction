package com.example.naturelink.Repository;

import com.example.naturelink.Entity.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourGuideRepository extends JpaRepository<TourGuide, Integer> {
}
