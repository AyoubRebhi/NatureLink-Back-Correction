package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Favorite;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Entity.Logement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndLogement(User user, Logement logement);
    List<Favorite> findByUser(User user);
    void deleteByUserAndLogement(User user, Logement logement);
}
