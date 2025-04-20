package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Service.IMonumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/monuments")
public class MonumentController {

    private static final String UPLOAD_DIR = "monument-Uploads/";

    @Autowired
    private IMonumentService monumentService;

    @PostMapping("/upload")
    public ResponseEntity<Monument> createMonumentWithImage(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("localisation") String localisation,
            @RequestParam("horairesOuverture") String horairesOuverture,
            @RequestParam("prixEntree") float prixEntree,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "modelInput", required = false) MultipartFile modelInputFile) {

        try {
            Monument monument = new Monument();
            monument.setNom(nom);
            monument.setDescription(description);
            monument.setLocalisation(localisation);
            monument.setHorairesOuverture(horairesOuverture);
            monument.setPrixEntree(prixEntree);

            Monument savedMonument;
            if (modelInputFile != null && !modelInputFile.isEmpty()) {
                savedMonument = monumentService.createMonumentWith3DModel(monument, imageFile, modelInputFile);
            } else {
                savedMonument = monumentService.createMonumentWithImage(monument, imageFile);
            }
            return ResponseEntity.ok(savedMonument);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Monument>> getAllMonuments() {
        List<Monument> monuments = monumentService.getAllMonuments();
        return ResponseEntity.ok(monuments);
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monument> getMonumentById(@PathVariable Integer id) {
        return monumentService.getMonumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonument(@PathVariable Integer id) {
        try {
            Optional<Monument> monumentOpt = monumentService.getMonumentById(id);
            if (monumentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            monumentService.deleteMonument(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Monument> updateMonumentWithImage(
            @PathVariable Integer id,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "localisation", required = false) String localisation,
            @RequestParam(value = "horairesOuverture", required = false) String horairesOuverture,
            @RequestParam(value = "prixEntree", required = false) Float prixEntree,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "modelInput", required = false) MultipartFile modelInputFile) {

        try {
            Monument monument = new Monument();
            monument.setId(id);
            if (nom != null) monument.setNom(nom);
            if (description != null) monument.setDescription(description);
            if (localisation != null) monument.setLocalisation(localisation);
            if (horairesOuverture != null) monument.setHorairesOuverture(horairesOuverture);
            if (prixEntree != null) monument.setPrixEntree(prixEntree);

            Monument updatedMonument;
            if (modelInputFile != null && !modelInputFile.isEmpty()) {
                updatedMonument = monumentService.updateMonumentWith3DModel(id, monument, modelInputFile);
            } else {
                updatedMonument = monumentService.updateMonumentWithImage(id, monument, imageFile);
            }
            return ResponseEntity.ok(updatedMonument);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Monument> createMonument(@RequestBody Monument monument) {
        return ResponseEntity.ok(monumentService.createMonument(monument));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Monument> updateMonument(@PathVariable Integer id, @RequestBody Monument monument) {
        return monumentService.getMonumentById(id)
                .map(existing -> {
                    monument.setId(id);
                    return ResponseEntity.ok(monumentService.updateMonument(monument));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}