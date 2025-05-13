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
 
public class MonumentController {

    private final IMonumentService monumentService;

    @Autowired
    public MonumentController(IMonumentService monumentService) {
        this.monumentService = monumentService;
    }

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @PostMapping
    public ResponseEntity<Monument> createMonument(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "openingHours", required = false) String openingHours,
            @RequestParam(value = "entranceFee", required = false) Float entranceFee,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Handle image upload
            String filename = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path imagePath = Paths.get(UPLOAD_DIR, filename);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, imageFile.getBytes());
            }

            // Create Monument object
            Monument monument = new Monument();
            monument.setName(name);
            monument.setDescription(description);
            monument.setLocation(location);
            monument.setOpeningHours(openingHours);
            monument.setEntranceFee(entranceFee);
            monument.setImage(filename);

            // Save Monument using service
            Monument savedMonument = monumentService.addMonument(monument);
            return ResponseEntity.ok(savedMonument);

        } catch (IOException e) {
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
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "openingHours", required = false) String openingHours,
            @RequestParam(value = "entranceFee", required = false) Float entranceFee,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Handle image upload
            String filename = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path imagePath = Paths.get(UPLOAD_DIR, filename);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, imageFile.getBytes());
            }

            // Create updated Monument object
            Monument updatedMonument = new Monument();
            updatedMonument.setName(name);
            updatedMonument.setDescription(description);
            updatedMonument.setLocation(location);
            updatedMonument.setOpeningHours(openingHours);
            updatedMonument.setEntranceFee(entranceFee);
            updatedMonument.setImage(filename);

            // Update Monument using service
            return monumentService.updateMonument(id, updatedMonument)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IOException e) {
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