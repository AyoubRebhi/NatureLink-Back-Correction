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

    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits() {
        try {
            List<Visit> visits = visitService.getAllVisits();
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Integer id) {
        try {
            return visitService.getVisitById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody Visit visit) {
        try {
            if (visit.getMonument() == null || visit.getMonument().getId() == null) {
                return ResponseEntity.badRequest().body("Monument est requis avec un ID.");
            }
            if (visit.getGuide() == null || visit.getGuide().getId() == null) {
                return ResponseEntity.badRequest().body("Guide est requis avec un ID.");
            }
            if (visit.getDate() == null || visit.getTime() == null || visit.getDuration() == null) {
                return ResponseEntity.badRequest().body("Date, heure, et durée sont requis.");
            }

            Visit createdVisit = visitService.createVisit(visit);
            return ResponseEntity.ok(createdVisit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable Integer id, @RequestBody Visit visit) {
        try {
            visit.setId(id);
            Visit updatedVisit = visitService.updateVisit(visit);
            return ResponseEntity.ok(updatedVisit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable Integer id) {
        try {
            visitService.deleteVisit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    @GetMapping("/monument/{monumentId}/with-relations")
    public ResponseEntity<?> getVisitsByMonumentIdWithRelations(@PathVariable Integer monumentId) {
        try {
            List<Visit> visits = visitService.getVisitsByMonumentIdWithRelations(monumentId);
            return visits.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(visits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }}
