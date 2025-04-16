package com.example.naturelink.Service;

import com.example.naturelink.Entity.TourGuide;
import com.example.naturelink.Repository.TourGuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TourGuideService implements ITourGuideService {

    @Autowired
    private TourGuideRepository tourGuideRepository;

    @Override
    public TourGuide createTourGuide(TourGuide tourGuide) {
        return tourGuideRepository.save(tourGuide);
    }

    @Override
    public List<TourGuide> getAllTourGuides() {
        return tourGuideRepository.findAll();
    }

    @Override
    public Optional<TourGuide> getTourGuideById(Integer id) {
        return tourGuideRepository.findById(id);
    }

    @Override
    public TourGuide updateTourGuide(Integer id, TourGuide tourGuide) {
        tourGuide.setId(id);
        return tourGuideRepository.save(tourGuide);
    }

    @Override
    public void deleteTourGuide(Integer id) {
        tourGuideRepository.deleteById(id);
    }
}
