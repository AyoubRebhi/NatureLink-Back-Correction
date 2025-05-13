package com.example.naturelink.Controller;

import com.example.naturelink.Entity.TourGuide;
import com.example.naturelink.Service.TourGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tourguides")
  // Optional: for frontend testing
public class TourGuideController {

    @Autowired
    private TourGuideService tourGuideService;


    @PostMapping
    public ResponseEntity<TourGuide> createTourGuide(@RequestBody TourGuide tourGuide) {
        TourGuide savedGuide = tourGuideService.createTourGuide(tourGuide);
        return new ResponseEntity<>(savedGuide, HttpStatus.CREATED);
    }


    @GetMapping
    public List<TourGuide> getAllTourGuides() {
        return tourGuideService.getAllTourGuides();
    }


    @GetMapping("/{id}")
    public ResponseEntity<TourGuide> getTourGuideById(@PathVariable Integer id) {
        Optional<TourGuide> guide = tourGuideService.getTourGuideById(id);
        return guide.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<TourGuide> updateTourGuide(@PathVariable Integer id, @RequestBody TourGuide tourGuide) {
        TourGuide updatedGuide = tourGuideService.updateTourGuide(id, tourGuide);
        return ResponseEntity.ok(updatedGuide);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourGuide(@PathVariable Integer id) {
        tourGuideService.deleteTourGuide(id);
        return ResponseEntity.noContent().build();
    }
}
