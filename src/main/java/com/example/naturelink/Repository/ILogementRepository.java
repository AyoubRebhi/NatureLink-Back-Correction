package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Logement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ILogementRepository extends JpaRepository<Logement, Integer> {
    List<Logement> findByProprietarield(Integer proprietarield);
}
