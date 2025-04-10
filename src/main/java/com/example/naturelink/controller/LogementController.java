package com.example.naturelink.controller;

import com.example.naturelink.dto.LogementRequestDTO;
import com.example.naturelink.entity.Logement;
import com.example.naturelink.service.LogementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logements")
public class LogementController {

    @Autowired
    private LogementService logementService;

    // Get all logements
    @GetMapping
    public List<Logement> getAllLogements() {
        return logementService.getAllLogements();
    }

    // Get logement by ID
    @GetMapping("/{id}")
    public ResponseEntity<Logement> getLogementById(@PathVariable Integer id) {
        Optional<Logement> logement = logementService.getLogementById(id);
        return logement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a new logement
    @PostMapping
    public Logement addLogement(@RequestBody LogementRequestDTO dto) {
        return logementService.addLogement(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Logement> updateLogement(
            @PathVariable Integer id,
            @Valid @RequestBody Logement logementDetails) {
        Logement updatedLogement = logementService.updateLogement(id, logementDetails);
        return ResponseEntity.ok(updatedLogement);
    }

    //check availability by date
    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isLogementAvailable(
            @PathVariable Integer id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        boolean isAvailable = logementService.isLogementAvailable(id, startDate, endDate);
        return ResponseEntity.ok(isAvailable);
    }


    // Delete a logement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogement(@PathVariable Integer id) {
        logementService.deleteLogement(id);
        return ResponseEntity.noContent().build();
    }
}