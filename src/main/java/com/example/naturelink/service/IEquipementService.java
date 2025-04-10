package com.example.naturelink.service;

import com.example.naturelink.entity.Equipement;

import java.util.List;
import java.util.Optional;

public interface IEquipementService {

    List<Equipement> getAllEquipements();

    Optional<Equipement> getEquipementById(Integer id);

    Equipement addEquipement(Equipement equipement);

    Equipement updateEquipement(Integer id, Equipement equipementDetails);

    void deleteEquipement(Integer id);
}
