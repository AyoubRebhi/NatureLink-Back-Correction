package com.example.naturelink.Service;

import com.example.naturelink.Entity.Restaurant;
import com.example.naturelink.Repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestaurantService implements IRestaurantService {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public Restaurant addRestaurant(Restaurant restaurant) {
        try {
            return restaurantRepository.save(restaurant);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du restaurant", e);
        }
    }

    @Override
    public Restaurant updateRestaurant(Long id, Restaurant restaurant, MultipartFile image) {
        if (restaurantRepository.existsById(id)) {
            restaurant.setId(id);  // Assurez-vous de conserver l'ID existant

            // Si une image est fournie, la traiter
            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);  // Sauvegarder l'image sur le disque
                restaurant.setImage(imagePath);  // Ajouter le chemin de l'image dans l'objet Restaurant
            }

            // Sauvegarder les informations mises à jour dans la base de données
            return restaurantRepository.save(restaurant);
        }
        return null;
    }

    private String saveImage(MultipartFile image) {
        try {
            // Créer un nom unique pour l'image
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(UPLOAD_DIR + filename);
            // Créer le répertoire si nécessaire
            Files.createDirectories(imagePath.getParent());
            // Sauvegarder l'image sur le disque
            Files.write(imagePath, image.getBytes());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
        }
    }

    @Override
    public boolean deleteRestaurant(Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Restaurant> getOpenRestaurantsBetween(LocalTime startTime, LocalTime endTime) {
        return restaurantRepository.findAll().stream()
                .filter(restaurant -> isRestaurantOpen(restaurant, startTime, endTime))
                .collect(Collectors.toList());
    }

    private boolean isRestaurantOpen(Restaurant restaurant, LocalTime startTime, LocalTime endTime) {
        try {
            // Validate horairesOuverture format
            if (restaurant.getHorairesOuverture() == null || !restaurant.getHorairesOuverture().matches("\\d{2}:\\d{2}-\\d{2}:\\d{2}")) {
                return false;
            }

            String[] horaires = restaurant.getHorairesOuverture().split("-");
            LocalTime openTime = LocalTime.parse(horaires[0].trim());
            LocalTime closeTime = LocalTime.parse(horaires[1].trim());

            // Handle overnight restaurants (closeTime before or at midnight)
            if (closeTime.isBefore(openTime) || closeTime.equals(LocalTime.MIDNIGHT)) {
                // Restaurant is open if time is after openTime OR before closeTime
                return (!startTime.isBefore(openTime) || startTime.isAfter(closeTime)) &&
                        (!endTime.isBefore(openTime) || endTime.isAfter(closeTime));
            } else {
                // Normal case: openTime to closeTime
                return !startTime.isBefore(openTime) && !endTime.isAfter(closeTime);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement des horaires pour le restaurant " + restaurant.getNom() + ": " + e.getMessage());
            return false;
        }
    }

}