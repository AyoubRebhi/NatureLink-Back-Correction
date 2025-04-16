package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {


    List<Menu> findByRestaurantId(Long restaurantId);

    Optional<Menu> findByIdAndRestaurantId(Long menuId, Long restaurantId);
}