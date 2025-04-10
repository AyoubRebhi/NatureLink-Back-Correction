package com.example.naturelink.repository;

import com.example.naturelink.entity.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipementRepository extends JpaRepository<Equipement, Integer> {
}
