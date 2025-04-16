package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipementRepository extends JpaRepository<Equipement, Integer> {
}
