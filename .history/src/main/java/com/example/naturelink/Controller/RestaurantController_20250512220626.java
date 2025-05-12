package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Restaurant;
import com.example.naturelink.Service.IRestaurantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private IRestaurantService restaurantService;

    @PostMapping("/upload")
    public ResponseEntity<Restaurant> createRestaurant(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("localisation") String localisation,
            @RequestParam("typeCuisine") String typeCuisine,
            @RequestParam("horairesOuverture") String horairesOuverture,
            @RequestParam("capacite") Integer capacite,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            // Save image
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path imagePath = Paths.get(UPLOAD_DIR + filename);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageFile.getBytes());

            // Create restaurant object
            Restaurant restaurant = new Restaurant();
            restaurant.setNom(nom);
            restaurant.setDescription(description);
            restaurant.setLocalisation(localisation);
            restaurant.setTypeCuisine(typeCuisine);
            restaurant.setHorairesOuverture(horairesOuverture);
            restaurant.setCapacite(capacite);
            restaurant.setImage(filename);

            Restaurant savedRestaurant = restaurantService.addRestaurant(restaurant);
            return ResponseEntity.ok(savedRestaurant);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
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
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        boolean deleted = restaurantService.deleteRestaurant(id);  // Utilisation du service
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "localisation", required = false) String localisation,
            @RequestParam(value = "typeCuisine", required = false) String typeCuisine,
            @RequestParam(value = "horairesOuverture", required = false) String horairesOuverture,
            @RequestParam(value = "capacite", required = false) Integer capacite,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Optional<Restaurant> existingRestaurantOpt = restaurantService.getRestaurantById(id);
            if (!existingRestaurantOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Restaurant existingRestaurant = existingRestaurantOpt.get();

            if (nom != null) existingRestaurant.setNom(nom);
            if (description != null) existingRestaurant.setDescription(description);
            if (localisation != null) existingRestaurant.setLocalisation(localisation);
            if (typeCuisine != null) existingRestaurant.setTypeCuisine(typeCuisine);
            if (horairesOuverture != null) existingRestaurant.setHorairesOuverture(horairesOuverture);
            if (capacite != null) existingRestaurant.setCapacite(capacite);

            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image if exists
                if (existingRestaurant.getImage() != null) {
                    Path oldImagePath = Paths.get(UPLOAD_DIR + existingRestaurant.getImage());
                    Files.deleteIfExists(oldImagePath);
                }

                // Save new image
                String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path imagePath = Paths.get(UPLOAD_DIR + filename);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, imageFile.getBytes());
                existingRestaurant.setImage(filename);
            }

            Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, existingRestaurant, imageFile);
            return ResponseEntity.ok(updatedRestaurant);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/open-now")
    public ResponseEntity<List<Restaurant>> getRestaurantsOpenNow() {
        LocalTime now = LocalTime.now();
        List<Restaurant> openRestaurants = restaurantService.getOpenRestaurantsBetween(now, now);
        return ResponseEntity.ok(openRestaurants);
    }

    @GetMapping("/open-between")
    public ResponseEntity<List<Restaurant>> getRestaurantsOpenBetween(
            @RequestParam String start,
            @RequestParam String end) {
        try {
            LocalTime startTime = LocalTime.parse(start);
            LocalTime endTime = LocalTime.parse(end);
            List<Restaurant> filteredRestaurants = restaurantService.getOpenRestaurantsBetween(startTime, endTime);
            return ResponseEntity.ok(filteredRestaurants);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}