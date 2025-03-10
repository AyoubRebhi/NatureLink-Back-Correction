package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import java.util.List;
import java.util.Optional;

public interface IMonumentService {
    Monument createMonument(Monument monument);
    Monument updateMonument(Monument monument);
    void deleteMonument(Integer id);
    List<Monument> getAllMonuments();
    Optional<Monument> getMonumentById(Integer id);
}
