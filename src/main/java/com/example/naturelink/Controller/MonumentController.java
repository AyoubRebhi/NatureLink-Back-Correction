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
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/monuments")
public class MonumentController {

    private static final String UPLOAD_DIR = "monument-uploads/";

    @Autowired
    private IMonumentService monumentService;

    @PostMapping("/upload")
    public ResponseEntity<Monument> createMonumentWithImage(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("localisation") String localisation,
            @RequestParam("horairesOuverture") String horairesOuverture,
            @RequestParam("prixEntree") float prixEntree,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            // Créer le monument avec l'image
            Monument monument = new Monument();
            monument.setNom(nom);
            monument.setDescription(description);
            monument.setLocalisation(localisation);
            monument.setHorairesOuverture(horairesOuverture);
            monument.setPrixEntree(prixEntree);

            Monument savedMonument = monumentService.createMonumentWithImage(monument, imageFile);
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
            System.out.println("Suppression du monument avec ID: " + id);

            // Vérifier si le monument existe
            Optional<Monument> monumentOpt = monumentService.getMonumentById(id);
            if (monumentOpt.isEmpty()) {
                System.out.println("Monument non trouvé");
                return ResponseEntity.notFound().build();
            }

            Monument monument = monumentOpt.get();
            String imagePathStr = monument.getImage();

            // Supprimer l'image seulement si c'est un chemin local (pas une URL)
            if (imagePathStr != null) {
                if (isLocalFilePath(imagePathStr)) {
                    try {
                        Path imagePath = Paths.get(UPLOAD_DIR).resolve(imagePathStr).normalize();
                        // Vérification de sécurité pour éviter les attaques de traversal path
                        if (!imagePath.startsWith(Paths.get(UPLOAD_DIR).normalize())) {
                            System.err.println("Tentative d'accès à un chemin non autorisé: " + imagePath);
                        } else {
                            Files.deleteIfExists(imagePath);
                            System.out.println("Image du monument supprimée : " + imagePath);
                        }
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la suppression de l'image : " + e.getMessage());
                        // Continuer quand même avec la suppression du monument
                    }
                } else {
                    System.out.println("Image externe (URL), pas de suppression locale nécessaire: " + imagePathStr);
                }
            }

            // Supprimer les données du monument
            monumentService.deleteMonument(id);
            System.out.println("Suppression réussie du monument");
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du monument:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Méthode utilitaire pour vérifier si le chemin est local
    private boolean isLocalFilePath(String path) {
        return path != null &&
                !path.startsWith("http://") &&
                !path.startsWith("https://") &&
                !path.startsWith("ftp://") &&
                !path.startsWith("www.") &&
                !path.contains("://");
    }



    @PutMapping("/{id}/image")
    public ResponseEntity<Monument> updateMonumentWithImage(
            @PathVariable Integer id,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "localisation", required = false) String localisation,
            @RequestParam(value = "horairesOuverture", required = false) String horairesOuverture,
            @RequestParam(value = "prixEntree", required = false) Float prixEntree,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Monument monument = new Monument();
            monument.setId(id);
            if (nom != null) monument.setNom(nom);
            if (description != null) monument.setDescription(description);
            if (localisation != null) monument.setLocalisation(localisation);
            if (horairesOuverture != null) monument.setHorairesOuverture(horairesOuverture);
            if (prixEntree != null) monument.setPrixEntree(prixEntree);

            Monument updatedMonument = monumentService.updateMonumentWithImage(id, monument, imageFile);
            return ResponseEntity.ok(updatedMonument);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoints sans gestion d'image
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