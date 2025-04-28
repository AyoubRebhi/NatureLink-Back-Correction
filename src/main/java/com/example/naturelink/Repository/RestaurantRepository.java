package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
     @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menus WHERE r.id = :id")
     Optional<Restaurant> findByIdWithMenus(@Param("id") Long id);
     @Query("SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.menus")
     List<Restaurant> findAllWithMenus();

}
