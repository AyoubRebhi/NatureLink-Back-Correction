package com.example.naturelink.service;

import com.example.naturelink.dto.LogementRequestDTO;
import com.example.naturelink.entity.Disponibility;
import com.example.naturelink.entity.Equipement;
import com.example.naturelink.entity.Logement;
import com.example.naturelink.repository.IEquipementRepository;
import com.example.naturelink.repository.ILogementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LogementService implements ILogementService {

    @Autowired
    private ILogementRepository logementRepository;

    @Autowired
    private IEquipementRepository equipementRepository;

    @Override
    public List<Logement> getAllLogements() {
        return logementRepository.findAll();
    }

    @Override
    public Optional<Logement> getLogementById(Integer id) {
        return logementRepository.findById(id);
    }

    @Override
    public Logement addLogement(LogementRequestDTO dto) {
        Logement logement = new Logement();

        logement.setTitre(dto.getTitre());
        logement.setDescription(dto.getDescription());
        logement.setLocation(dto.getLocation());
        logement.setPrice(dto.getPrice());
        logement.setImage(dto.getImage());
        logement.setProprietarield(dto.getProprietarield());
        logement.setPhone(dto.getPhone());
        logement.setEmail(dto.getEmail());
        logement.setSocialMedia(dto.getSocialMedia());

        List<Equipement> allEquipements = new ArrayList<>();

        if (dto.getEquipementIds() != null) {
            allEquipements.addAll(equipementRepository.findAllById(dto.getEquipementIds()));
        }

        if (dto.getNewEquipements() != null) {
            for (String name : dto.getNewEquipements()) {
                Equipement newEq = new Equipement();
                newEq.setNom(name);
                equipementRepository.save(newEq);
                allEquipements.add(newEq);
            }
        }

        logement.setEquipements(allEquipements);

        return logementRepository.save(logement);
    }

    @Override
    public Logement updateLogement(Integer id, Logement logementDetails) {
        Logement logement = logementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logement not found with id: " + id));

        logement.setTitre(logementDetails.getTitre());
        logement.setDescription(logementDetails.getDescription());
        logement.setLocation(logementDetails.getLocation());
        logement.setEquipements(logementDetails.getEquipements());
        logement.setPrice(logementDetails.getPrice());
        logement.setImage(logementDetails.getImage());
        logement.setProprietarield(logementDetails.getProprietarield());
        logement.setPhone(logementDetails.getPhone());
        logement.setEmail(logementDetails.getEmail());
        logement.setSocialMedia(logementDetails.getSocialMedia());

        return logementRepository.save(logement);
    }

    public boolean isLogementAvailable(Integer logementId, LocalDate startDate, LocalDate endDate) {
        Optional<Logement> logementOpt = logementRepository.findById(logementId);
        if (logementOpt.isEmpty()) return false;

        Logement logement = logementOpt.get();

        for (Disponibility dispo : logement.getDisponibilities()) {
            if ((startDate.isEqual(dispo.getStartDate()) || startDate.isAfter(dispo.getStartDate())) &&
                    (endDate.isEqual(dispo.getEndDate()) || endDate.isBefore(dispo.getEndDate()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteLogement(Integer id) {
        logementRepository.deleteById(id);
    }
}
