package com.example.naturelink.Service;

import com.example.naturelink.Entity.Clothing;
import com.example.naturelink.Entity.Destination;
import com.example.naturelink.Repository.IDestinationRepository;
import com.example.naturelink.Repository.iClothingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ClothingService {
    @Autowired
    private iClothingRepository clothingRepository;

    @Autowired
    private IDestinationRepository destinationRepository;

    @Autowired
    private WeatherService weatherService;

    // Utilisation du même dossier uploads
    private final Path rootLocation = Paths.get("uploads");

    public List<Clothing> getAllClothingItems() {
        return clothingRepository.findAll();
    }

    public List<Clothing> getClothingsByDestination(Long destinationId) {
        Destination destination = clothingRepository.findById(destinationId)
                .map(Clothing::getDestination)
                .orElse(null);

        if (destination == null) {
            return List.of();
        }

        String season = weatherService.getCurrentSeason(destination.getNom());
        return clothingRepository.findBySeason(season);
    }

    public Clothing addClothing(Clothing clothing) {
        return clothingRepository.save(clothing);
    }

    public Clothing addClothingWithImage(String name, String description,
                                         String season, Long destinationId,
                                         MultipartFile image) {
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }

                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Files.copy(image.getInputStream(), this.rootLocation.resolve(filename));
                imageUrl = "/uploads/" + filename;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        Clothing clothing = new Clothing();
        clothing.setName(name);
        clothing.setDescription(description);
        clothing.setSeason(season);
        clothing.setImageUrl(imageUrl);
        clothing.setDestination(destination);

        return clothingRepository.save(clothing);
    }
}