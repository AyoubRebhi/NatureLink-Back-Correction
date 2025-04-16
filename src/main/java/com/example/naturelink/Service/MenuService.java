package com.example.naturelink.Service;

import com.example.naturelink.Entity.Menu;
import com.example.naturelink.Entity.Restaurant;
import com.example.naturelink.Repository.MenuRepository;
import com.example.naturelink.Repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MenuService implements IMenuService {

    @Value("${menu.images.directory}")
    private String uploadDir;

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository, RestaurantRepository restaurantRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public Menu createMenu(Menu menu, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        menu.setRestaurant(restaurant);
        return menuRepository.save(menu);
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }



    @Override
    public Menu updateMenu(Long id, Menu updatedMenu) {
        return menuRepository.findById(id)
                .map(existingMenu -> {
                    existingMenu.setPlats(updatedMenu.getPlats());
                    existingMenu.setPrixMoyen(updatedMenu.getPrixMoyen());
                    existingMenu.setIngredientsDetails(updatedMenu.getIngredientsDetails());

                    if (updatedMenu.getImage() != null && !updatedMenu.getImage().isEmpty()) {
                        try {
                            deleteOldImage(existingMenu.getImage());
                        } catch (IOException e) {
                            System.err.println("Could not delete old image: " + e.getMessage());
                        }
                        existingMenu.setImage(updatedMenu.getImage());
                    }
                    return menuRepository.save(existingMenu);
                })
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + id));
    }

    @Override
    public boolean deleteMenu(Long id) {
        return menuRepository.findById(id)
                .map(menu -> {
                    if (menu.getImage() != null && !menu.getImage().isEmpty()) {
                        try {
                            deleteOldImage(menu.getImage());
                        } catch (IOException e) {
                            System.err.println("Could not delete image: " + e.getMessage());
                        }
                    }
                    menuRepository.delete(menu);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<Menu> getMenusByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;

        Path path = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    private void deleteOldImage(String filename) throws IOException {
        if (filename != null && !filename.isBlank()) {
            Path path = Paths.get(uploadDir).resolve(filename).normalize();
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }
    public Optional<Menu> getMenuById(Long restaurantId, Long menuId) {
        return menuRepository.findByIdAndRestaurantId(menuId, restaurantId);
    }
}