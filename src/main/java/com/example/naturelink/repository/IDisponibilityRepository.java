package com.example.naturelink.repository;

import com.example.naturelink.entity.Disponibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDisponibilityRepository extends JpaRepository<Disponibility, Integer> {
    List<Disponibility> findByLogementId(Integer logementId);
}
