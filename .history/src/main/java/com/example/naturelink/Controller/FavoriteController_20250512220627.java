package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Favorite;
import com.example.naturelink.Service.FavoriteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")  // Enable CORS for this controller

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add/{logementId}")
    public Favorite addFavorite(@RequestParam Integer userId, @PathVariable Long logementId) {
        return favoriteService.addFavorite(userId, logementId);
    }
    @Transactional
    @DeleteMapping("/{logementId}")
    public void removeFavorite(@RequestParam Integer userId, @PathVariable Long logementId) {
        favoriteService.removeFavorite(userId, logementId);
    }

    @GetMapping
    public List<Favorite> getFavorites(@RequestParam Integer userId) {
        return favoriteService.getFavorites(userId);
    }

    @GetMapping("/{logementId}/is-favorite")
    public boolean isFavorite(@RequestParam Integer userId, @PathVariable Long logementId) {
        return favoriteService.isFavorite(userId, logementId);
    }
}
