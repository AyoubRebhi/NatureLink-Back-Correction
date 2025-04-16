package com.example.naturelink.Controller;

import com.example.naturelink.dto.LogementRequestDTO;
import com.example.naturelink.Entity.Equipement;
import com.example.naturelink.Entity.Logement;
import com.example.naturelink.Entity.LogementType;
import com.example.naturelink.Service.EquipementService;
import com.example.naturelink.Service.LogementService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/logements")
public class LogementController {

    @Autowired
    private LogementService logementService;

    @Autowired
    private EquipementService equipementService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping
    public List<Logement> getAllLogements() {
        return logementService.getAllLogements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Logement> getLogementById(@PathVariable Integer id) {
        Optional<Logement> logement = logementService.getLogementById(id);
        return logement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Logement> createLogement(@RequestBody LogementRequestDTO logementDTO) {
        Logement createdLogement = logementService.addLogement(logementDTO);
        messagingTemplate.convertAndSend("/topic/logements", createdLogement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLogement);
    }

    @PostMapping("/upload")
    public ResponseEntity<Logement> createLogementWithImages(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("price") Double price,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("socialMedia") String socialMedia,
            @RequestParam("type") String type,
            @RequestParam("proprietaireId") Integer proprietaireId,
            @RequestParam("capacity") Integer capacity,  // Added capacity
            @RequestParam(value = "singleRooms", required = false) Integer singleRooms,
            @RequestParam(value = "doubleRooms", required = false) Integer doubleRooms,
            @RequestParam(value = "equipementIds", required = false) List<Integer> equipementIds,
            @RequestParam(value = "newEquipements", required = false) List<String> newEquipements,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles

    ) {
        try {
            List<String> savedImageNames = new ArrayList<>();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                System.out.println("Received " + imageFiles.size() + " image(s).");
                for (MultipartFile file : imageFiles) {
                    System.out.println("File name: " + file.getOriginalFilename() + ", File size: " + file.getSize());
                    savedImageNames.add(saveImage(file));
                }
            } else {
                System.out.println("No images received.");
            }

            LogementType logementType = LogementType.valueOf(type.toUpperCase());

            Logement logement = new Logement();
            logement.setTitre(titre);
            logement.setDescription(description);
            logement.setLocation(location);
            logement.setPrice(price);
            logement.setPhone(phone);
            logement.setEmail(email);
            logement.setSocialMedia(socialMedia);
            logement.setType(logementType);
            logement.setProprietarield(proprietaireId);
            logement.setSingleRooms(singleRooms);
            logement.setDoubleRooms(doubleRooms);
            logement.setCapacity(capacity);  // Set capacity

            logement.setImages(savedImageNames);

            List<Equipement> equipements = new ArrayList<>();
            if (newEquipements != null) {
                for (String newEq : newEquipements) {
                    Equipement eq = new Equipement();
                    eq.setName(newEq);
                    eq.setDescription("");
                    equipements.add(equipementService.createEquipement(eq));
                }
            }

            if (equipementIds != null) {
                for (Integer id : equipementIds) {
                    equipementService.getEquipementById(id).ifPresent(equipements::add);
                }
            }

            logement.setEquipements(equipements);

            Logement savedLogement = logementService.createLogement(logement);
            messagingTemplate.convertAndSend("/topic/logements", savedLogement);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLogement);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    private String saveImage(MultipartFile imageFile) throws IOException {
        String uploadDir = "uploads/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();  // Create folder if it doesn't exist
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, imageFile.getBytes());  // Save the file

        return fileName;  // Return the file name to be stored in the Logement Entity
    }
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename);
            Resource resource = (Resource) new FileSystemResource(filePath.toFile());

            if (!((FileSystemResource) resource).exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Assuming your images are JPEG
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/upload/{id}")
    public ResponseEntity<Logement> updateLogementWithImage(
            @PathVariable Integer id,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("price") Double price,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("socialMedia") String socialMedia,
            @RequestParam("type") String type,
            @RequestParam("capacity") Integer capacity,  // Added capacity
            @RequestParam("proprietaireId") Integer proprietaireId,
            @RequestParam(value = "singleRooms", required = false) Integer singleRooms,
            @RequestParam(value = "doubleRooms", required = false) Integer doubleRooms,
            @RequestParam(value = "equipementIds", required = false) List<Integer> equipementIds,
            @RequestParam(value = "newEquipements", required = false) List<String> newEquipements,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
            @RequestParam(value = "removedImages", required = false) List<String> removedImages // Add this parameter

    ) {
        try {
            Logement existingLogement = logementService.getLogementById(id).orElse(null);
            if (existingLogement == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Handle image removal if any
            if (removedImages != null && !removedImages.isEmpty()) {
                for (String imageName : removedImages) {
                    // Delete the image from the filesystem or database
                    Path imagePath = Paths.get("your-image-directory", imageName);
                    try {
                        Files.deleteIfExists(imagePath);
                        // Optionally, remove it from the logement's image list
                        existingLogement.getImages().remove(imageName);
                    } catch (IOException e) {
                        System.out.println("Error deleting image: " + imageName);
                    }
                }
            }

            // Save new images if any
            List<String> savedImageNames = new ArrayList<>();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                System.out.println("Received " + imageFiles.size() + " image(s).");
                for (MultipartFile file : imageFiles) {
                    System.out.println("File name: " + file.getOriginalFilename() + ", File size: " + file.getSize());
                    savedImageNames.add(saveImage(file));
                }
            }

            // If new images were uploaded, replace the old images with the new ones.
            List<String> updatedImages = (savedImageNames.isEmpty()) ? existingLogement.getImages() : savedImageNames;

            LogementType logementType = LogementType.valueOf(type.toUpperCase());

            // Update existing logement details
            existingLogement.setTitre(titre);
            existingLogement.setDescription(description);
            existingLogement.setLocation(location);
            existingLogement.setPrice(price);
            existingLogement.setPhone(phone);
            existingLogement.setEmail(email);
            existingLogement.setSocialMedia(socialMedia);
            existingLogement.setType(logementType);
            existingLogement.setCapacity(capacity);
            existingLogement.setProprietarield(proprietaireId);
            existingLogement.setSingleRooms(singleRooms);
            existingLogement.setDoubleRooms(doubleRooms);

            existingLogement.setImages(updatedImages);

            // Update equipements
            List<Equipement> equipements = new ArrayList<>();
            if (newEquipements != null) {
                for (String newEq : newEquipements) {
                    Equipement eq = new Equipement();
                    eq.setName(newEq);
                    eq.setDescription("");
                    equipements.add(equipementService.createEquipement(eq));
                }
            }

            if (equipementIds != null) {
                for (Integer equipementId : equipementIds) {
                    equipementService.getEquipementById(equipementId).ifPresent(equipements::add);
                }
            }

            existingLogement.setEquipements(equipements);

            // Save the updated logement
            Logement updatedLogement = logementService.updateLogement(id, existingLogement);

            return ResponseEntity.ok(updatedLogement);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Logement> updateLogement(@PathVariable Integer id, @RequestBody Logement logementDetails) {
        try {
            Logement updated = logementService.updateLogement(id, logementDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogement(@PathVariable Integer id) {
        logementService.deleteLogement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proprietaire/{proprietaireId}")
    public ResponseEntity<List<Logement>> getLogementsByProprietaireId(@PathVariable Integer proprietaireId) {
        List<Logement> logements = logementService.getLogementsByProprietaireId(proprietaireId);
        return ResponseEntity.ok(logements);
    }
    @DeleteMapping("/{logementId}/uploads/{imageName}")
    public ResponseEntity<Logement> removeImage(@PathVariable Integer logementId, @PathVariable String imageName) {
        try {
            Logement updatedLogement = logementService.removeImageFromLogement(logementId, imageName);
            return ResponseEntity.ok(updatedLogement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
