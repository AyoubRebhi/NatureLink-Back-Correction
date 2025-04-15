package com.example.naturelink.service;

import com.example.naturelink.dto.LogementRequestDTO;
import com.example.naturelink.entity.Logement;

import java.util.List;
import java.util.Optional;

public interface ILogementService {

    // Get all Logements
    List<Logement> getAllLogements();

    // Get Logement by ID
    Optional<Logement> getLogementById(Integer id);

    // Create a new Logement from DTO
    Logement addLogement(LogementRequestDTO dto);

    // Create a new Logement from Entity (Optional use)
    Logement createLogement(Logement logement);

    // Update an existing Logement
    Logement updateLogement(Integer id, Logement logementDetails);

    // Delete a Logement by ID
    void deleteLogement(Integer id);
    Logement updateLogementWithImage(Integer id, Logement logementDetails);

}
