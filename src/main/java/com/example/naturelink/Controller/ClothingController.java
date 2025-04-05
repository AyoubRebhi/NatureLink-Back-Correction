package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Clothing;
import com.example.naturelink.Service.ClothingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clothing")
public class ClothingController {
    @Autowired
    private ClothingService clothingService;

    @GetMapping
    public List<Clothing> getAllClothingItems() {
        return clothingService.getAllClothingItems();
    }

    @GetMapping("/destination/{destinationId}")
    public List<Clothing> getClothingByDestination(@PathVariable Long destinationId) {
        return clothingService.getClothingsByDestination(destinationId);
    }

    // Nouvelle méthode pour ajouter avec image
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Clothing> addClothingWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("season") String season,
            @RequestParam("destinationId") Long destinationId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Clothing clothing = clothingService.addClothingWithImage(name, description, season, destinationId, image);
        return ResponseEntity.ok(clothing);
    }

    // Garder l'ancienne méthode pour la compatibilité
    @PostMapping("/json")
    public Clothing addClothing(@RequestBody Clothing clothing) {
        return clothingService.addClothing(clothing);
    }
}