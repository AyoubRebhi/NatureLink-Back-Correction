package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMonumentService {
    Monument createMonument(Monument monument);
    Monument createMonumentWithImage(Monument monument, MultipartFile imageFile) throws IOException;
    Monument createMonumentWith3DModel(Monument monument, MultipartFile imageFile, MultipartFile modelFile) throws IOException;
    Monument updateMonument(Monument monument);
    Monument updateMonumentWithImage(Integer id, Monument monument, MultipartFile imageFile) throws IOException;
    Monument updateMonumentWith3DModel(Integer id, Monument monument, MultipartFile modelFile) throws IOException;
    void deleteMonument(Integer id);
    List<Monument> getAllMonuments();
    Optional<Monument> getMonumentById(Integer id);
}