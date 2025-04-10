package com.example.naturelink.controller;

import com.example.naturelink.entity.Unit;
import com.example.naturelink.service.UnitService;
import com.example.naturelink.entity.Logement;
import com.example.naturelink.service.LogementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/units")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private LogementService logementService;

    // Create a new Unit
    @PostMapping
    public ResponseEntity<Object> createUnit(@RequestBody @Valid Unit unit) {
        System.out.println("Received Unit: " + unit);

        if (unit.getLogement() == null || unit.getLogement().getId() == null) {
            return new ResponseEntity<>("Logement ID is required", HttpStatus.BAD_REQUEST);
        }

        Optional<Logement> logementOpt = logementService.getLogementById(unit.getLogement().getId());

        if (!logementOpt.isPresent()) {
            return new ResponseEntity<>("Logement not found", HttpStatus.NOT_FOUND);
        }

        unit.setLogement(logementOpt.get());
        Unit createdUnit = unitService.createUnit(unit);
        return new ResponseEntity<>(createdUnit, HttpStatus.CREATED);
    }


    // Get all Units
    @GetMapping
    public List<Unit> getAllUnits() {
        return unitService.getAllUnits();
    }

    // Get a Unit by its ID
    @GetMapping("/{unitId}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Integer unitId) {
        Unit unit = unitService.getUnitById(unitId);
        if (unit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(unit, HttpStatus.OK);
    }

    // Update an existing Unit
    @PutMapping("/{unitId}")
    public ResponseEntity<Unit> updateUnit(@PathVariable Integer unitId, @RequestBody @Valid Unit unitDetails) {
        Unit updatedUnit = unitService.updateUnit(unitId, unitDetails);
        if (updatedUnit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUnit, HttpStatus.OK);
    }

    // Delete a Unit by its ID
    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Integer unitId) {
        try {
            unitService.deleteUnit(unitId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
