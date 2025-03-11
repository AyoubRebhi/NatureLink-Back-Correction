package com.example.naturelink.Controller;
import com.example.naturelink.Entity.Clothing;
import com.example.naturelink.Service.ClothingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clothing")
public class ClothingController {
    @Autowired
    private ClothingService clothingService;

    @GetMapping
    public List<Clothing> getAllClothingItems() {
        return clothingService.getAllClothingItems();
    }

    @GetMapping("/destination/{destinationId}")
    public List<Clothing> getClothingByDestination(@PathVariable Long destinationId) {
        return clothingService.getClothingByDestination(destinationId);
    }

    @PostMapping
    public Clothing addClothing(@RequestBody Clothing clothing) {
        return clothingService.addClothing(clothing);
    }
}