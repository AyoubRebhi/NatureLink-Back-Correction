package com.example.naturelink.Controller;

import com.example.naturelink.dto.PackDTO;
import com.example.naturelink.dto.RatingDTO;
import com.example.naturelink.Entity.Pack;
import com.example.naturelink.Service.IPackService;
import com.example.naturelink.Service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/packs")
@CrossOrigin(origins = "*") // Optional: for frontend testing
public class PackController {

    private final IPackService packService;
    private RatingService ratingService;  // Inject RatingService here
    @Autowired
    public PackController(IPackService packService, RatingService ratingService) {
        this.packService = packService;
        this.ratingService = ratingService;  // Injection du RatingService
    }




    public PackController(IPackService packService) {
        this.packService = packService;
    }
    // ➕ Add a new pack
    @PostMapping
    public ResponseEntity<?> addPack(@RequestBody PackDTO packDTO) {
        packService.addPack(packDTO);
        return ResponseEntity.ok().build(); // ✅ correct
    }


    // ✏️ Update an existing pack
    @PutMapping("/update/{id}")
    public ResponseEntity<Pack> updatePack(@PathVariable Long id, @RequestBody PackDTO dto) {
        try {
            Pack updatedPack = packService.updatePack(id, dto);
            return ResponseEntity.ok(updatedPack);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ❌ Delete a pack by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePack(@PathVariable Long id) {
        try {
            packService.deletePack(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 📦 Get all packs
    @GetMapping
    public ResponseEntity<List<PackDTO>> getAllPacks() {
        List<PackDTO> packs = packService.getAllPacks();
        return ResponseEntity.ok(packs); // ✅ No casting needed
    }


    // 🔍 Get pack by ID
    @GetMapping("/{id}")
    public ResponseEntity<PackDTO> getPackById(@PathVariable Long id) {
        PackDTO pack = packService.getPackById(id);
        return ResponseEntity.ok(pack);
    }
    @PostMapping("/ratings")
    public ResponseEntity<String> addRating(@RequestBody RatingDTO ratingDTO) {
        try {
            ratingService.addOrUpdateRating(ratingDTO.getPackId(), ratingDTO.getRatingValue(), ratingDTO.getUserId());
            return ResponseEntity.ok("{\"message\": \"Rating submitted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error adding rating: " + e.getMessage() + "\"}");
        }
    }

    // Get the average rating of a pack
    @GetMapping("/{id}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        double avgRating = ratingService.getAverageRatingForPack(id);
        return ResponseEntity.ok(avgRating);
    }
}


