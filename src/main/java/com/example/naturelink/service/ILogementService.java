package com.example.naturelink.service;

import com.example.naturelink.dto.LogementRequestDTO;
import com.example.naturelink.entity.Logement;

import java.util.List;
import java.util.Optional;

public interface ILogementService {
    List<Logement> getAllLogements();
    Optional<Logement> getLogementById(Integer id);
    Logement addLogement(LogementRequestDTO dto);
    Logement updateLogement(Integer id, Logement logementDetails);
    void deleteLogement(Integer id);
}
