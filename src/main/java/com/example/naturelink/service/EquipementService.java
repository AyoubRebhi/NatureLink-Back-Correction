package com.example.naturelink.service;

import com.example.naturelink.entity.Equipement;
import com.example.naturelink.repository.IEquipementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipementService {

    @Autowired
    private IEquipementRepository equipementRepository;

    public List<Equipement> getAllEquipements() {
        return equipementRepository.findAll();
    }

    public Optional<Equipement> getEquipementById(Integer id) {
        return equipementRepository.findById(id);
    }

    public Equipement createEquipement(Equipement equipement) {
        return equipementRepository.save(equipement);
    }

    public Equipement updateEquipement(Integer id, Equipement equipementDetails) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipement not found"));

        equipement.setName(equipementDetails.getName());
        equipement.setDescription(equipementDetails.getDescription());

        return equipementRepository.save(equipement);
    }

    public void deleteEquipement(Integer id) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipement not found"));
        equipementRepository.delete(equipement);
    }
}
