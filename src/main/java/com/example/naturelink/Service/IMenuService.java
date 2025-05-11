package com.example.naturelink.Service;

import com.example.naturelink.Entity.Menu;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IMenuService {
    Menu createMenu(Menu menu, Long restaurantId, MultipartFile image) throws Exception;
    Menu updateMenu(Long id, Menu menu, MultipartFile image) throws Exception;
    boolean deleteMenu(Long id);
    List<Menu> getMenusByRestaurant(Long restaurantId);
    Optional<Menu> getMenuById(Long restaurantId, Long menuId);
    List<String> detectAllergens(String ingredientsDetails);
    List<String> detectAllergensWithAI(String ingredientsDetails);
    List<Menu> filterMenusByAllergen(Long restaurantId, String allergen) throws Exception;
    String answerMenuQuestion(Long restaurantId, String question);
}