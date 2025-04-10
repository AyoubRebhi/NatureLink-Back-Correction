package com.example.naturelink.service;

import com.example.naturelink.dto.DisponibilityRequestDto;
import com.example.naturelink.entity.Disponibility;
import com.example.naturelink.entity.Logement;
import com.example.naturelink.repository.IDisponibilityRepository;
import com.example.naturelink.repository.ILogementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisponibilityService implements IDisponibilityService {

    @Autowired
    private IDisponibilityRepository disponibilityRepository;

    @Autowired
    private ILogementRepository logementRepository;

    @Override
    public List<Disponibility> getAllDisponibilities() {
        return disponibilityRepository.findAll();
    }

    @Override
    public Disponibility getDisponibilityById(Integer id) {
        return disponibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibility not found"));
    }

    @Override
    public Disponibility save(Disponibility disponibility) {
        return disponibilityRepository.save(disponibility);
    }

    @Override
    public Disponibility addDisponibility(DisponibilityRequestDto dto) {
        Logement logement = logementRepository.findById(dto.getLogementId())
                .orElseThrow(() -> new RuntimeException("Logement not found"));

        Disponibility disponibility = new Disponibility();
        disponibility.setStartDate(dto.getStartDate());
        disponibility.setEndDate(dto.getEndDate());
        disponibility.setLogement(logement); // Set the Logement entity instead of just the ID

        return disponibilityRepository.save(disponibility);
    }

    @Override
    public Disponibility updateDisponibility(Integer id, Disponibility updatedDisponibility) {
        Disponibility existing = getDisponibilityById(id);
        existing.setStartDate(updatedDisponibility.getStartDate());
        existing.setEndDate(updatedDisponibility.getEndDate());
        return disponibilityRepository.save(existing);
    }

    @Override
    public void deleteDisponibility(Integer id) {
        disponibilityRepository.deleteById(id);
    }

    @Override
    public List<Disponibility> getDisponibilitiesByLogement(Integer logementId) {
        return disponibilityRepository.findByLogementId(logementId);
    }
}
