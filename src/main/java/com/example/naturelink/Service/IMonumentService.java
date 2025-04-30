package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import java.util.List;
import java.util.Optional;

public interface IMonumentService {
    Monument addMonument(Monument monument);
    Optional<Monument> getMonumentById(Integer id);
    List<Monument> getAllMonuments();
    Optional<Monument> updateMonument(Integer id, Monument updatedMonument);
    boolean deleteMonument(Integer id);
    Monument enrichMonumentData(String monumentName) throws Exception;
}