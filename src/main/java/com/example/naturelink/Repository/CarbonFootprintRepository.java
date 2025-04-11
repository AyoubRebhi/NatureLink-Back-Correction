package com.example.naturelink.Repository;

import com.example.naturelink.Entity.CarbonFootprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarbonFootprintRepository extends JpaRepository<CarbonFootprint, Long> {
    List<CarbonFootprint> findByTransportType(String transportType);
}
