package com.example.naturelink.service;

import com.example.naturelink.dto.DisponibilityRequestDto;
import com.example.naturelink.entity.Disponibility;

import java.util.List;

public interface IDisponibilityService {
    List<Disponibility> getAllDisponibilities();
    Disponibility getDisponibilityById(Integer id);
    Disponibility addDisponibility(DisponibilityRequestDto dto);
    Disponibility updateDisponibility(Integer id, Disponibility updatedDisponibility);
    void deleteDisponibility(Integer id);
    Disponibility save(Disponibility disponibility);  // Add this method

    List<Disponibility> getDisponibilitiesByLogement(Integer logementId);
}
