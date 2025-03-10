package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Visit;
import com.example.naturelink.Service.IVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private IVisitService visitService;

    // Récupérer toutes les visites
    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits() {
        List<Visit> visits = visitService.getAllVisits();
        return ResponseEntity.ok(visits);
    }

    // Récupérer une visite par son id
    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Integer id) {
        return visitService.getVisitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Créer une nouvelle visite
    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        Visit createdVisit = visitService.createVisit(visit);
        return ResponseEntity.ok(createdVisit);
    }

    // Mettre à jour une visite existante
    @PutMapping("/{id}")
    public ResponseEntity<Visit> updateVisit(@PathVariable Integer id, @RequestBody Visit visit) {
        visit.setId(id); // Assurez-vous que l'ID correspond à celui passé dans l'URL
        Visit updatedVisit = visitService.updateVisit(visit);
        return ResponseEntity.ok(updatedVisit);
    }

    // Supprimer une visite par son id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Integer id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}
