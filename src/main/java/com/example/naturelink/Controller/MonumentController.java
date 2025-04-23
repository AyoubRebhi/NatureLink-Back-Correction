package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Service.IMonumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monuments")
@CrossOrigin(origins = "http://localhost:4200")
public class MonumentController {

    private final IMonumentService monumentService;

    @Autowired
    public MonumentController(IMonumentService monumentService) {
        this.monumentService = monumentService;
    }

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @PostMapping
    public ResponseEntity<Monument> createMonument(@RequestBody Monument monument) {
        try {
            // Sauvegarde directe si pas d'image (le champ image peut être une URL ou un nom de fichier par défaut)
            Monument savedMonument = monumentService.addMonument(monument);
            return ResponseEntity.ok(savedMonument);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Monument> getMonument(@PathVariable Integer id) {
        return monumentService.getMonumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Monument>> getAllMonuments() {
        return ResponseEntity.ok(monumentService.getAllMonuments());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Monument> updateMonument(
            @PathVariable Integer id,
            @RequestBody Monument updatedMonument) {

        try {
            // Mise à jour via service (assure-toi que ce service remplace bien les champs)
            return monumentService.updateMonument(id, updatedMonument)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonument(@PathVariable Integer id) {
        return monumentService.deleteMonument(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/enrich/{name}")
    public ResponseEntity<Monument> enrichMonument(@PathVariable String name) {
        try {
            Monument enrichedMonument = monumentService.enrichMonumentData(name);
            return ResponseEntity.ok(enrichedMonument);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}