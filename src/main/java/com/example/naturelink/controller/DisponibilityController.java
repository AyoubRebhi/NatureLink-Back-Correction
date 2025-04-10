package com.example.naturelink.controller;

import com.example.naturelink.dto.DisponibilityRequestDto;
import com.example.naturelink.entity.Disponibility;
import com.example.naturelink.entity.Logement;
import com.example.naturelink.service.IDisponibilityService;
import com.example.naturelink.service.ILogementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disponibilities")
public class DisponibilityController {

    @Autowired
    private IDisponibilityService disponibilityService;

    @Autowired
    private ILogementService logementService;  // Service to get logement details

    @GetMapping
    public List<Disponibility> getAllDisponibilities() {
        return disponibilityService.getAllDisponibilities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Disponibility> getDisponibilityById(@PathVariable Integer id) {
        return ResponseEntity.ok(disponibilityService.getDisponibilityById(id));
    }

    @GetMapping("/logement/{logementId}")
    public List<Disponibility> getDisponibilitiesByLogement(@PathVariable Integer logementId) {
        return disponibilityService.getDisponibilitiesByLogement(logementId);
    }

    @PostMapping("/add/{logementId}")
    public ResponseEntity<Disponibility> addDisponibility(
            @PathVariable Integer logementId,
            @RequestBody DisponibilityRequestDto request) {

        // Fetch the logement by ID
        Optional<Logement> logementOpt = logementService.getLogementById(logementId);

        if (logementOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if logement not found
        }

        // Get the Logement from the Optional
        Logement logement = logementOpt.get();

        // Create a new Disponibility and set its properties
        Disponibility disponibility = new Disponibility();
        disponibility.setStartDate(request.getStartDate());
        disponibility.setEndDate(request.getEndDate());

        // Associate the Disponibility with the Logement
        disponibility.setLogement(logement); // Set the Logement entity

        // Save the disponibility and return the response
        Disponibility savedDisponibility = disponibilityService.save(disponibility);
        return ResponseEntity.ok(savedDisponibility);
    }


    @PutMapping("/{id}")
    public Disponibility updateDisponibility(@PathVariable Integer id, @RequestBody Disponibility disponibility) {
        return disponibilityService.updateDisponibility(id, disponibility);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibility(@PathVariable Integer id) {
        disponibilityService.deleteDisponibility(id);
        return ResponseEntity.noContent().build();
    }
}
