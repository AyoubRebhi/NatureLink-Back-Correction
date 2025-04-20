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
    private static final String MODEL_UPLOAD_DIR = "monument-Uploads/models/";

    private final MonumentRepository monumentRepository;
    private final VisitRepository visitRepository;

    @Autowired
    public MonumentService(MonumentRepository monumentRepository, VisitRepository visitRepository) {
        this.monumentRepository = monumentRepository;
        this.visitRepository = visitRepository;
    }

    private void validateMonument(Monument monument) {
        if (monument.getNom() == null || monument.getNom().isBlank()) {
            throw new IllegalArgumentException("Monument name is required");
        }
        if (monument.getPrixEntree() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    @Override
    public Monument createMonument(Monument monument) {
        validateMonument(monument);
        return monumentRepository.save(monument);
    }

    @Override
    public Monument createMonumentWithImage(Monument monument, MultipartFile imageFile) throws IOException {
        validateMonument(monument);
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = saveImage(imageFile);
            monument.setImage(filename);
        }
        return monumentRepository.save(monument);
    }

    @Override
    public Monument createMonumentWith3DModel(Monument monument, MultipartFile imageFile, MultipartFile modelFile) throws IOException {
        validateMonument(monument);
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFilename = saveImage(imageFile);
            monument.setImage(imageFilename);
        }
        if (modelFile != null && !modelFile.isEmpty()) {
            String modelFilename = save3DModel(modelFile);
            monument.setModel3DUrl(modelFilename);
            monument.setModel3DFormat(modelFile.getOriginalFilename().endsWith(".glb") ? "GLB" : "GLTF");
            monument.setModel3DStatus("ready");
            monument.setModelScale("1.0"); // Échelle par défaut
            monument.setModelPosition("0,0,0"); // Position par défaut
        }
        return monumentRepository.save(monument);
    }

    @Override
    public Monument updateMonument(Monument monument) {
        if (monumentRepository.existsById(monument.getId())) {
            validateMonument(monument);
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
            validateMonument(monument);
            return monumentRepository.save(monument);
        }
        throw new EntityNotFoundException("Monument with id " + id + " not found");
    }

    @Override
    public Monument updateMonumentWith3DModel(Integer id, Monument monument, MultipartFile modelFile) throws IOException {
        Optional<Monument> existingMonument = monumentRepository.findById(id);
        if (existingMonument.isPresent()) {
            if (modelFile != null && !modelFile.isEmpty()) {
                if (existingMonument.get().getModel3DUrl() != null) {
                    delete3DModel(existingMonument.get().getModel3DUrl());
                }
                String modelFilename = save3DModel(modelFile);
                monument.setModel3DUrl(modelFilename);
                monument.setModel3DFormat(modelFile.getOriginalFilename().endsWith(".glb") ? "GLB" : "GLTF");
                monument.setModel3DStatus("ready");
                monument.setModelScale("1.0");
                monument.setModelPosition("0,0,0");
            } else {
                monument.setModel3DUrl(existingMonument.get().getModel3DUrl());
                monument.setModel3DFormat(existingMonument.get().getModel3DFormat());
                monument.setModel3DStatus(existingMonument.get().getModel3DStatus());
            }
            monument.setId(id);
            validateMonument(monument);
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
                    deleteImage(monument.get().getImage());
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression de l'image du monument: " + e.getMessage());
                }
            }
            if (monument.get().getModel3DUrl() != null) {
                try {
                    delete3DModel(monument.get().getModel3DUrl());
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression du modèle 3D: " + e.getMessage());
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

    private String save3DModel(MultipartFile model) throws IOException {
        String filename = UUID.randomUUID() + "_" + model.getOriginalFilename();
        Path modelPath = Paths.get(MODEL_UPLOAD_DIR + filename);
        Files.createDirectories(modelPath.getParent());
        Files.write(modelPath, model.getBytes());
        return filename;
    }

    private void deleteImage(String filename) throws IOException {
        Path imagePath = Paths.get(UPLOAD_DIR + filename);
        Files.deleteIfExists(imagePath);
    }

    private void delete3DModel(String filename) throws IOException {
        Path modelPath = Paths.get(MODEL_UPLOAD_DIR + filename);
        Files.deleteIfExists(modelPath);
    }
}