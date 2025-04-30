package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PackRepository extends JpaRepository<Pack, Long> {
    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.logements WHERE p.id = :id")
    Optional<Pack> findByIdWithLogements(@Param("id") Long id);

    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.evenements WHERE p.id = :id")
    Optional<Pack> findByIdWithEvenements(@Param("id") Long id);

    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.restaurants WHERE p.id = :id")
    Optional<Pack> findByIdWithRestaurants(@Param("id") Long id);

    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.transports WHERE p.id = :id")
    Optional<Pack> findByIdWithTransports(@Param("id") Long id);

    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.activities WHERE p.id = :id")
    Optional<Pack> findByIdWithActivities(@Param("id") Long id);}
