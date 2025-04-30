package com.example.naturelink.Service;


import com.example.naturelink.Entity.Favorite;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Entity.Logement;
import com.example.naturelink.Repository.FavoriteRepository;
import com.example.naturelink.Repository.ILogementRepository;
import com.example.naturelink.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ILogementRepository logementRepository;

    public Favorite addFavorite(Integer userId, Long logementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Logement logement = logementRepository.findById(logementId)
                .orElseThrow(() -> new EntityNotFoundException("Logement not found"));

        return favoriteRepository.findByUserAndLogement(user, logement)
                .orElseGet(() -> favoriteRepository.save(new Favorite(null, user, logement)));
    }

    public void removeFavorite(Integer userId, Long logementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Logement logement = logementRepository.findById(logementId)
                .orElseThrow(() -> new EntityNotFoundException("Logement not found"));

        favoriteRepository.deleteByUserAndLogement(user, logement);
    }

    public List<Favorite> getFavorites(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return favoriteRepository.findByUser(user);
    }

    public boolean isFavorite(Integer userId, Long logementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Logement logement = logementRepository.findById(logementId)
                .orElseThrow(() -> new EntityNotFoundException("Logement not found"));

        return favoriteRepository.findByUserAndLogement(user, logement).isPresent();
    }
}
