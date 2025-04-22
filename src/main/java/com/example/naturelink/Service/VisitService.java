package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Entity.TourGuide;
import com.example.naturelink.Entity.Visit;
import com.example.naturelink.Repository.MonumentRepository;
import com.example.naturelink.Repository.TourGuideRepository;
import com.example.naturelink.Repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VisitService implements IVisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private MonumentRepository monumentRepository;

    @Autowired
    private TourGuideRepository tourGuideRepository;

    @Override
    public Visit createVisit(Visit visit) {
        if (visit.getMonument() != null && visit.getMonument().getId() != null) {
            Monument monument = monumentRepository.findById(visit.getMonument().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Monument non trouvé"));
            visit.setMonument(monument);
        }

        if (visit.getGuide() != null && visit.getGuide().getId() != null) {
            TourGuide guide = tourGuideRepository.findById(visit.getGuide().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Guide non trouvé"));
            visit.setGuide(guide);
        }

        return visitRepository.save(visit);
    }

    @Override
    public Visit updateVisit(Visit visit) {
        if (!visitRepository.existsById(visit.getId())) {
            throw new IllegalArgumentException("Visite non trouvée avec l'ID: " + visit.getId());
        }

        Visit existingVisit = visitRepository.findById(visit.getId()).get();
        existingVisit.setDate(visit.getDate());
        existingVisit.setTime(visit.getTime());
        existingVisit.setPrice(visit.getPrice());
        existingVisit.setDuration(visit.getDuration());

        if (visit.getMonument() != null && !visit.getMonument().getId().equals(existingVisit.getMonument().getId())) {
            Monument monument = monumentRepository.findById(visit.getMonument().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Monument non trouvé"));
            existingVisit.setMonument(monument);
        }

        if (visit.getGuide() != null && !visit.getGuide().getId().equals(existingVisit.getGuide().getId())) {
            TourGuide guide = tourGuideRepository.findById(visit.getGuide().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Guide non trouvé"));
            existingVisit.setGuide(guide);
        }

        return visitRepository.save(existingVisit);
    }

    @Override
    public void deleteVisit(Integer id) {
        if (!visitRepository.existsById(id)) {
            throw new IllegalArgumentException("Visite non trouvée avec l'ID: " + id);
        }
        visitRepository.deleteById(id);
    }

    @Override
    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    @Override
    public Optional<Visit> getVisitById(Integer id) {
        return visitRepository.findById(id);
    }
   
    @Override
    public List<Visit> getAllVisitsWithRelations() {
        return visitRepository.findAllWithRelations();
    }
    @Override
    public List<Visit> getVisitsByMonumentId(Integer monumentId) {
        if (!monumentRepository.existsById(monumentId)) {
            throw new IllegalArgumentException("Monument non trouvé");
        }
        return visitRepository.findByMonumentId(monumentId);
    }

    @Override
    public List<Visit> getVisitsByMonumentIdWithRelations(Integer monumentId) {
        if (!monumentRepository.existsById(monumentId)) {
            throw new IllegalArgumentException("Monument non trouvé");
        }
        return visitRepository.findByMonumentIdWithRelations(monumentId);
    }
}