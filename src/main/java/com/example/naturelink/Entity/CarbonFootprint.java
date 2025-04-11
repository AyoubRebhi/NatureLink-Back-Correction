package com.example.naturelink.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

// CarbonFootprint.java
@Entity
@Data

public class CarbonFootprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double distance; // en km
    private String transportType;
    private double carbonFootprint; // en kg CO2
    private LocalDateTime date;

    // Getters et Setters
}
