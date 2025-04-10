package com.example.naturelink.service;

import com.example.naturelink.entity.Unit;

import java.util.List;

public interface IUnitService {
    // Create a new Unit
    Unit createUnit(Unit unit);

    // Get all Units
    List<Unit> getAllUnits();

    // Find a Unit by its ID
    Unit getUnitById(Integer unitId);

    // Update an existing Unit
    Unit updateUnit(Integer unitId, Unit unitDetails);

    // Delete a Unit by its ID
    void deleteUnit(Integer unitId);
}
