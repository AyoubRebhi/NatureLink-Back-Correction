package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Repository.MonumentRepository;
import com.example.naturelink.Repository.VisitRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonumentService implements IMonumentService {

    private static final String UPLOAD_DIR = "monument-Uploads/";

    private final MonumentRepository monumentRepository;
    private final VisitRepository visitRepository;


    @Autowired
    public MonumentService(MonumentRepository monumentRepository, VisitRepository visitRepository) {
        this.monumentRepository = monumentRepository;
        this.visitRepository = visitRepository;

    }

    @Override
    public Monument createMonument(Monument monument) {
        return monumentRepository.save(monument);
    }

    @Override
    public Monument createMonumentWithImage(Monument monument, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = saveImage(imageFile);
            monument.setImage(filename);
        }
        return monumentRepository.save(monument);
    }


    @Override
    public Monument updateMonument(Monument monument) {
        if (monumentRepository.existsById(monument.getId())) {
            return monumentRepository.save(monument);
        }
        throw new EntityNotFoundException("Monument with id " + monument.getId() + " not found");
    }

    @Override
    public Monument updateMonumentWithImage(Integer id, Monument monument, MultipartFile imageFile) throws IOException {
        Optional<Monument> existingMonument = monumentRepository.findById(id);
        if (existingMonument.isPresent()) {
            if (imageFile != null && !imageFile.isEmpty()) {
                if (existingMonument.get().getImage() != null) {
                    deleteImage(existingMonument.get().getImage());
                }
                String filename = saveImage(imageFile);
                monument.setImage(filename);
            } else {
                monument.setImage(existingMonument.get().getImage());
            }
            monument.setId(id);
            return monumentRepository.save(monument);
        }
        throw new EntityNotFoundException("Monument with id " + id + " not found");
    }



    @Override
    @Transactional
    public void deleteMonument(Integer id) {
        Optional<Monument> monument = monumentRepository.findById(id);
        if (monument.isPresent()) {
            visitRepository.deleteAllByMonumentId(id);
            if (monument.get().getImage() != null) {
                try {
                    Path imagePath = Paths.get(UPLOAD_DIR + monument.get().getImage());
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression de l'image du monument: " + e.getMessage());
                }
            }

            monumentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Monument with id " + id + " not found");
        }
    }

    @Override
    public List<Monument> getAllMonuments() {
        return monumentRepository.findAll();
    }

    @Override
    public Optional<Monument> getMonumentById(Integer id) {
        return monumentRepository.findById(id);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path imagePath = Paths.get(UPLOAD_DIR + filename);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, image.getBytes());
        return filename;
    }

    private void deleteImage(String filename) throws IOException {
        Path imagePath = Paths.get(UPLOAD_DIR + filename);
        Files.deleteIfExists(imagePath);
    }
}