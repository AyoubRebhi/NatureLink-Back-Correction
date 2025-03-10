package com.example.naturelink.Service;
import com.example.naturelink.Entity.Visit;
import com.example.naturelink.Repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitService implements IVisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Override
    public Visit createVisit(Visit visit) {
        return visitRepository.save(visit);
    }

    @Override
    public Visit updateVisit(Visit visit) {
        // Vous pouvez vérifier l'existence de la visite ici si nécessaire
        return visitRepository.save(visit);
    }

    @Override
    public void deleteVisit(Integer id) {
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
}