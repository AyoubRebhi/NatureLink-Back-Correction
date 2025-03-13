package com.example.naturelink.repository;

import com.example.naturelink.entity.Logement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILogementRepository extends JpaRepository<Logement, Integer> {
    // Custom queries can be added here if needed
}