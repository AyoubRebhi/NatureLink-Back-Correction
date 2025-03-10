package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Service.IMonumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monuments")
public class MonumentController {

    @Autowired
    private IMonumentService monumentService;

    // Récupérer tous les monuments
    @GetMapping
    public ResponseEntity<List<Monument>> getAllMonuments() {
        List<Monument> monuments = monumentService.getAllMonuments();
        return ResponseEntity.ok(monuments);
    }

    // Récupérer un monument par son id
    @GetMapping("/{id}")
    public ResponseEntity<Monument> getMonumentById(@PathVariable Integer id) {
        return monumentService.getMonumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Créer un nouveau monument
    @PostMapping
    public ResponseEntity<Monument> createMonument(@RequestBody Monument monument) {
        Monument createdMonument = monumentService.createMonument(monument);
        return ResponseEntity.ok(createdMonument);
    }

    // Mettre à jour un monument existant
    @PutMapping("/{id}")
    public ResponseEntity<Monument> updateMonument(@PathVariable Integer id, @RequestBody Monument monument) {
        monument.setId(id); // Assurez-vous que l'ID correspond à celui passé dans l'URL
        Monument updatedMonument = monumentService.updateMonument(monument);
        return ResponseEntity.ok(updatedMonument);
    }

    // Supprimer un monument par son id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonument(@PathVariable Integer id) {
        monumentService.deleteMonument(id);
        return ResponseEntity.noContent().build();
    }
}
