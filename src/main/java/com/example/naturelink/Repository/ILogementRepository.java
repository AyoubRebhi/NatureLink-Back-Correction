package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Logement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ILogementRepository extends JpaRepository<Logement, Long> {
    List<Logement> findByProprietarield(Integer proprietarield);
    List<Logement> findByImagesIn(List<String> imageNames);
    }

