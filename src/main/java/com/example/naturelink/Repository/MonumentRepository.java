package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Monument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonumentRepository extends JpaRepository<Monument, Integer> {
    Optional<Monument> findByName(String name);
}