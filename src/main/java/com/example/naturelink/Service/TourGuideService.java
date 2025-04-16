package Service;

import Entity.TourGuide;
import Repository.TourGuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TourGuideService {
    private final TourGuideRepository tourGuideRepository;

    public TourGuideService(TourGuideRepository tourGuideRepository) {
        this.tourGuideRepository = tourGuideRepository;
    }

    // Ajouter un guide
    public TourGuide addTourGuide(TourGuide tourGuide) {
        return tourGuideRepository.save(tourGuide);
    }

    // Récupérer tous les guides
    public List<TourGuide> getAllTourGuides() {
        return tourGuideRepository.findAll();
    }

    // Récupérer un guide par ID
    public Optional<TourGuide> getTourGuideById(Integer id) {
        return tourGuideRepository.findById(id);
    }



    // Supprimer un guide
    public void deleteTourGuide(Integer id) {
        tourGuideRepository.deleteById(id);
    }
}
