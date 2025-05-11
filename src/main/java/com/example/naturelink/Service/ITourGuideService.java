package com.example.naturelink.Service;

import com.example.naturelink.Entity.TourGuide;

import java.util.List;
import java.util.Optional;

public interface ITourGuideService {
    TourGuide createTourGuide(TourGuide tourGuide);
    List<TourGuide> getAllTourGuides();
    Optional<TourGuide> getTourGuideById(Integer id);
    TourGuide updateTourGuide(Integer id, TourGuide tourGuide);
    void deleteTourGuide(Integer id);
}