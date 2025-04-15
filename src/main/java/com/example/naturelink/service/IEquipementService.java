package com.example.naturelink.service;

import com.example.naturelink.entity.Equipement;

import java.util.List;
import java.util.Optional;

public interface IEquipementService {

    // Get all Equipements
    List<Equipement> getAllEquipements();

    // Get Equipement by ID
    Optional<Equipement> getEquipementById(Integer id);

    // Create a new Equipement
    Equipement createEquipement(Equipement equipement);

    // Update an existing Equipement
    Equipement updateEquipement(Integer id, Equipement equipementDetails);

    // Delete an Equipement by ID
    void deleteEquipement(Integer id);
}
