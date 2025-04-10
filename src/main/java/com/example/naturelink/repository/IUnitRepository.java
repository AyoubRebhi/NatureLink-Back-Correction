package com.example.naturelink.repository;

import com.example.naturelink.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnitRepository extends JpaRepository<Unit, Integer> {
    // You can define custom queries here if needed, like finding units by logement, etc.
}
