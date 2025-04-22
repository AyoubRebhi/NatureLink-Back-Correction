package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Logement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ILogementRepository extends JpaRepository<Logement, Integer> {
    List<Logement> findByProprietarield(Integer proprietarield);
        Optional<Logement> findByImagesContaining(String imageFilename);
    }

