package com.example.naturelink.Service;

import com.example.naturelink.Entity.Clothing;
import com.example.naturelink.Repository.iClothingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClothingService {
    @Autowired
    private iClothingRepository clothingRepository;

    public List<Clothing> getAllClothingItems() {
        return clothingRepository.findAll();
    }

    public List<Clothing> getClothingByDestination(Long destinationId) {
        return clothingRepository.findByDestinationId(destinationId);
    }

    public Clothing addClothing(Clothing clothing) {
        return clothingRepository.save(clothing);
    }
}