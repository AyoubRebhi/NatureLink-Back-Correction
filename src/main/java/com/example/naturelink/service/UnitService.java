package com.example.naturelink.service;

import com.example.naturelink.entity.Unit;
import com.example.naturelink.repository.IUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitService implements IUnitService{

    @Autowired
    private IUnitRepository unitRepository;

    // Create a new Unit
    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    // Get all Units
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    // Find a Unit by its ID
    public Unit getUnitById(Integer unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    // Update an existing Unit
    public Unit updateUnit(Integer unitId, Unit unitDetails) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        unit.setUnitType(unitDetails.getUnitType());
        unit.setPricePerNight(unitDetails.getPricePerNight());
        unit.setLogement(unitDetails.getLogement()); // assuming you update the logement as well
        return unitRepository.save(unit);
    }

    // Delete a Unit by ID
    public void deleteUnit(Integer unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        unitRepository.delete(unit);
    }
}
