package com.example.naturelink.Service;

import com.example.naturelink.Entity.Destination;
import com.example.naturelink.Entity.Food;
import com.example.naturelink.Repository.FoodRepository;
import com.example.naturelink.Repository.IDestinationRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private WeatherService weatherService;
    @Autowired

    private IDestinationRepository destinationRepository;

    private final Path rootLocation = Paths.get("uploads");


    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public Food getFoodById(Long id) {
        return foodRepository.findById(id).orElse(null);
    }

    public List<Food> getFoodsByDestination(Long destinationId) {
        // Récupère la destination et sa ville
        Destination destination = foodRepository.findById(destinationId)
                .map(Food::getDestination)
                .orElse(null);

        if (destination == null) {
            return List.of();  // Retourner une liste vide si destination non trouvée
        }

        // Récupère la saison à partir de la ville de la destination
        String season = weatherService.getCurrentSeason(destination.getNom());

        // Filtrer les plats par saison
        return foodRepository.findBySeason(season);
    }



    public Food addFoodWithImage(String name, String description, String season,
                                 Long destinationId, MultipartFile image) {
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }

                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Files.copy(image.getInputStream(), this.rootLocation.resolve(filename));
                imageUrl = "/uploads/" + filename;  // Même chemin que pour les posts
            } catch (IOException e) {
                // Gérer l'exception selon vos besoins
                e.printStackTrace();
            }
        }

        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        Food food = Food.builder()
                .nom(name)
                .description(description)
                .season(season)
                .imageUrl(imageUrl)
                .destination(destination)
                .build();

        return foodRepository.save(food);
    }


    public Food addFood(Food food) {
        return foodRepository.save(food);
    }

    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }
}
