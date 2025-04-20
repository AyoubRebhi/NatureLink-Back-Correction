package com.example.naturelink.Service;

import com.example.naturelink.Entity.Restaurant;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface IRestaurantService {

    List<Restaurant> getAllRestaurants();

    Optional<Restaurant> getRestaurantById(Long id);

    Restaurant addRestaurant(Restaurant restaurant);

    Restaurant updateRestaurant(Long id, Restaurant restaurant, MultipartFile image);

    List<Restaurant> getOpenRestaurantsBetween(LocalTime start, LocalTime end);

    boolean deleteRestaurant(Long id);
}