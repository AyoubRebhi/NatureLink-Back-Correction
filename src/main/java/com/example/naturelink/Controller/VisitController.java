package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Visit;
import com.example.naturelink.Service.IVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private IVisitService visitService;

    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits() {
        List<Visit> visits = visitService.getAllVisits();
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Integer id) {
        return visitService.getVisitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        try {
            if (visit.getMonument() == null || visit.getGuide() == null) {
                return ResponseEntity.badRequest().build();
            }
            Visit createdVisit = visitService.createVisit(visit);
            return ResponseEntity.ok(createdVisit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visit> updateVisit(@PathVariable Integer id, @RequestBody Visit visit) {
        try {
            visit.setId(id);
            Visit updatedVisit = visitService.updateVisit(visit);
            return ResponseEntity.ok(updatedVisit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Integer id) {
        try {
            visitService.deleteVisit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/monument/{monumentId}/with-relations")
    public ResponseEntity<List<Visit>> getVisitsByMonumentIdWithRelations(@PathVariable Integer monumentId) {
        try {
            List<Visit> visits = visitService.getVisitsByMonumentIdWithRelations(monumentId);
            return visits.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(visits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}