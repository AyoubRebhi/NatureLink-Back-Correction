package com.example.naturelink.Service;

import com.example.naturelink.dto.LogementRequestDTO;
import com.example.naturelink.Entity.Equipement;
import com.example.naturelink.Entity.Logement;
import com.example.naturelink.Repository.ILogementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LogementService implements ILogementService {

    @Autowired
    private ILogementRepository logementRepository;

    @Autowired
    private EquipementService equipementService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Override
    public List<Logement> getAllLogements() {
        return logementRepository.findAll();
    }

    @Override
    public Optional<Logement> getLogementById(Integer id) {
        return logementRepository.findById(Long.valueOf(id));
    }

    @Override
    public Logement addLogement(LogementRequestDTO dto) {
        Logement logement = new Logement();

        logement.setTitre(dto.getTitre());
        logement.setDescription(dto.getDescription());
        logement.setLocation(dto.getLocation());
        logement.setPrice(dto.getPrice());
        logement.setProprietarield(dto.getProprietarield());
        logement.setPhone(dto.getPhone());
        logement.setEmail(dto.getEmail());
        logement.setSocialMedia(dto.getSocialMedia());
        logement.setSingleRooms(dto.getSingleRooms());
        logement.setDoubleRooms(dto.getDoubleRooms());
        logement.setCapacity(dto.getCapacity()); // Set capacity


        // Handle images
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            logement.setImages(dto.getImages());
        }

        // Set type
        if (dto.getType() != null) {
            logement.setType(dto.getType());
        }

        List<Equipement> equipements = new ArrayList<>();

        // Add new equipements
        if (dto.getNewEquipements() != null && !dto.getNewEquipements().isEmpty()) {
            for (String newEq : dto.getNewEquipements()) {
                Equipement equipement = new Equipement();
                equipement.setName(newEq);
                equipement.setDescription("");
                Equipement savedEq = equipementService.createEquipement(equipement);
                equipements.add(savedEq);
            }
        }

        // Add existing equipements
        if (dto.getEquipementIds() != null && !dto.getEquipementIds().isEmpty()) {
            for (Integer eqId : dto.getEquipementIds()) {
                equipementService.getEquipementById(eqId).ifPresent(equipements::add);
            }
        }

        logement.setEquipements(equipements);

        return logementRepository.save(logement);
    }

    @Override
    public Logement createLogement(Logement logement) {
        return logementRepository.save(logement);
    }
    public Logement updateLogementWithImage(Integer id, Logement logementDetails) {
        Logement logement = logementRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Logement not found"));

        logement.setTitre(logementDetails.getTitre());
        logement.setDescription(logementDetails.getDescription());
        logement.setLocation(logementDetails.getLocation());
        logement.setType(logementDetails.getType());
        logement.setPrice(logementDetails.getPrice());
        logement.setProprietarield(logementDetails.getProprietarield());
        logement.setPhone(logementDetails.getPhone());
        logement.setEmail(logementDetails.getEmail());
        logement.setCapacity(logementDetails.getCapacity());
        logement.setSocialMedia(logementDetails.getSocialMedia());
        logement.setSingleRooms(logementDetails.getSingleRooms());
        logement.setDoubleRooms(logementDetails.getDoubleRooms());

        // Handle image updates (replace old images with new ones)
        if (logementDetails.getImages() != null && !logementDetails.getImages().isEmpty()) {
            logement.setImages(logementDetails.getImages());
        }

        logement.setEquipements(logementDetails.getEquipements());

        return logementRepository.save(logement);
    }

    @Override
    public Logement updateLogement(Integer id, Logement logementDetails) {
        Logement logement = logementRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Logement not found"));

        logement.setTitre(logementDetails.getTitre());
        logement.setDescription(logementDetails.getDescription());
        logement.setLocation(logementDetails.getLocation());
        logement.setType(logementDetails.getType());
        logement.setPrice(logementDetails.getPrice());
        logement.setProprietarield(logementDetails.getProprietarield());
        logement.setPhone(logementDetails.getPhone());
        logement.setEmail(logementDetails.getEmail());
        logement.setSocialMedia(logementDetails.getSocialMedia());
        logement.setSingleRooms(logementDetails.getSingleRooms());
        logement.setDoubleRooms(logementDetails.getDoubleRooms());

        // Handle image updates (allow new image list to replace old ones)
        if (logementDetails.getImages() != null && !logementDetails.getImages().isEmpty()) {
            logement.setImages(logementDetails.getImages());
        }

        logement.setEquipements(logementDetails.getEquipements());

        return logementRepository.save(logement);
    }
    @Override
    public void deleteLogement(Integer id) {
        logementRepository.deleteById(Long.valueOf(id));
    }

    public List<Logement> getLogementsByProprietaireId(Integer proprietaireId) {
        return logementRepository.findByProprietarield(proprietaireId);
    }
    private void deleteImageFile(String imageName) {
        try {
            Path imagePath = Paths.get("uploads/" + imageName);
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
    }

    public Logement removeImageFromLogement(Integer logementId, String imageName) {
        try {
            // Your logic to find the logement by ID and remove the image from database or associated data
            Logement logement = logementRepository.findById(Long.valueOf(logementId)).orElseThrow(() -> new RuntimeException("Logement not found"));

            // Remove the image from the database if necessary
            logement.getImages().remove(imageName);
            logementRepository.save(logement); // Save the updated logement to database

            // Delete the image from the file system
            Path imagePath = Paths.get("uploads", imageName);
            Files.deleteIfExists(imagePath); // Deletes the file if it exists

            return logement; // Return the updated logement
        } catch (IOException e) {
            throw new RuntimeException("Error removing image from the server", e);
        }
    }
    public List<Logement> findLogementsByImages(List<String> imageNames) {
        // Query the database for logements that contain any of the image names
        return logementRepository.findByImagesIn(imageNames);
    }
    /**
     * Finds all logements that have an image name matching a part of the provided image names.
     * @param imageNames List of image names (can be partial names).
     * @return List of logements that have at least one image matching the provided names (partial or full).
     */
    public List<Logement> findLogementsByImageNames(List<String> imageNames) {
        // Fetch all logements from the repository (can optimize if needed)
        List<Logement> allLogements = logementRepository.findAll();

        // Filter logements that have an image name containing any of the provided partial image names
        List<Logement> matchingLogements = allLogements.stream()
                .filter(logement -> logement.getImages().stream()
                        .anyMatch(image -> imageNames.stream().anyMatch(image::contains)))  // Match partial name
                .collect(Collectors.toList());

        return matchingLogements;
    }

}

