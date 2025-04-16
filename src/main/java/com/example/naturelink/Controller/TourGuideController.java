package Controller;
import Entity.TourGuide;
import Service.TourGuideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tourGuides")
public class TourGuideController {
    private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }


    @PostMapping
    public ResponseEntity<TourGuide> addTourGuide(@RequestBody TourGuide tourGuide) {
        TourGuide createdGuide = tourGuideService.addTourGuide(tourGuide);
        return ResponseEntity.ok(createdGuide);
    }


    @GetMapping
    public ResponseEntity<List<TourGuide>> getAllTourGuides() {
        return ResponseEntity.ok(tourGuideService.getAllTourGuides());
    }


    @GetMapping("/{id}")
    public ResponseEntity<TourGuide> getTourGuideById(@PathVariable Integer id) {
        Optional<TourGuide> tourGuide = tourGuideService.getTourGuideById(id);
        return tourGuide.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourGuide(@PathVariable Integer id) {
        tourGuideService.deleteTourGuide(id);
        return ResponseEntity.noContent().build();
    }
}
