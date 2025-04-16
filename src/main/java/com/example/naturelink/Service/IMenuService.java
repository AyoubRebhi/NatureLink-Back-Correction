package com.example.naturelink.Service;

import com.example.naturelink.Entity.Menu;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMenuService {
    Menu createMenu(Menu menu, Long restaurantId);
    List<Menu> getAllMenus();
    Optional<Menu> getMenuById(Long restaurantId, Long menuId);
    Menu updateMenu(Long id, Menu updatedMenu);
    boolean deleteMenu(Long id);
    List<Menu> getMenusByRestaurant(Long restaurantId);
    String storeImage(MultipartFile file) throws IOException;
}
