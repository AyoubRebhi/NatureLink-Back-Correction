package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Menu;
import com.example.naturelink.Service.IMenuService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "http://localhost:4200") // Frontend Angular
public class MenuController {

    private final IMenuService menuService;

    @Value("${menu.images.directory}")
    private String uploadDir;

    @Autowired
    public MenuController(IMenuService menuService) {
        this.menuService = menuService;
    }

    // Initialisation du dossier upload à la création du contrôleur
    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier upload", e);
        }
    }

    // Méthode pour créer un menu (avec une image optionnelle)
    @PostMapping(value = "/restaurant/{restaurantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Menu> createMenu(
            @PathVariable Long restaurantId,
            @RequestParam("plats") String plats,
            @RequestParam("prixMoyen") float prixMoyen,
            @RequestParam("ingredientsDetails") String ingredientsDetails,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Enregistrement de l'image si elle existe
            String filename = (imageFile != null && !imageFile.isEmpty())
                    ? menuService.storeImage(imageFile) : null;

            Menu menu = new Menu();
            menu.setPlats(plats);
            menu.setPrixMoyen(prixMoyen);
            menu.setIngredientsDetails(ingredientsDetails);
            menu.setImage(filename);

            Menu createdMenu = menuService.createMenu(menu, restaurantId);
            return new ResponseEntity<>(createdMenu, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Méthode pour mettre à jour un menu (avec une image optionnelle)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Menu> updateMenu(
            @PathVariable Long id,
            @RequestParam("plats") String plats,
            @RequestParam("prixMoyen") float prixMoyen,
            @RequestParam("ingredientsDetails") String ingredientsDetails,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            String filename = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                filename = menuService.storeImage(imageFile);
            }

            Menu updatedMenu = new Menu();
            updatedMenu.setPlats(plats);
            updatedMenu.setPrixMoyen(prixMoyen);
            updatedMenu.setIngredientsDetails(ingredientsDetails);
            updatedMenu.setImage(filename);

            Menu result = menuService.updateMenu(id, updatedMenu);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Méthode pour supprimer un menu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        boolean deleted = menuService.deleteMenu(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // Méthode pour récupérer les menus d'un restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Menu>> getMenusByRestaurant(@PathVariable Long restaurantId) {
        try {
            List<Menu> menus = menuService.getMenusByRestaurant(restaurantId);
            return menus.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/restaurant/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Menu> getMenuById(
            @PathVariable Long restaurantId,
            @PathVariable Long menuId) {
        try {
            Optional<Menu> menu = menuService.getMenuById(restaurantId, menuId);
            return menu.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
