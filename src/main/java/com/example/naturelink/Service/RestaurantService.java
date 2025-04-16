package com.example.naturelink.Service;

import com.example.naturelink.Entity.Restaurant;
import com.example.naturelink.Repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}
