package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Menu;
import com.example.naturelink.Repository.MenuRepository;
import com.example.naturelink.Service.IMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/menus")
 
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);
    private final IMenuService menuService;
    private final MenuRepository menuRepository;

    @Autowired
    public MenuController(IMenuService menuService, MenuRepository menuRepository) {
        this.menuService = menuService;
        this.menuRepository = menuRepository;
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> createMenu(
            @PathVariable Long restaurantId,
            @RequestParam("plats") String plats,
            @RequestParam("prixMoyen") float prixMoyen,
            @RequestParam("ingredientsDetails") String ingredientsDetails,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        if (restaurantId == null || restaurantId <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid restaurant ID",
                    "details", "ID must be a positive number"
            ));
        }

        if (plats == null || plats.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Dish name is required"
            ));
        }

        try {
            Menu menu = new Menu();
            menu.setPlats(plats.trim());
            menu.setPrixMoyen(prixMoyen);
            menu.setIngredientsDetails(ingredientsDetails.trim());
            menu.setRestaurantId(restaurantId);

            Menu createdMenu = menuService.createMenu(menu, restaurantId, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMenu);
        } catch (Exception e) {
            log.error("Error creating menu", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating menu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(
            @PathVariable Long id,
            @RequestParam("plats") String plats,
            @RequestParam("prixMoyen") float prixMoyen,
            @RequestParam("ingredientsDetails") String ingredientsDetails,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Menu updatedMenu = new Menu();
            updatedMenu.setPlats(plats.trim());
            updatedMenu.setPrixMoyen(prixMoyen);
            updatedMenu.setIngredientsDetails(ingredientsDetails.trim());

            Menu result = menuService.updateMenu(id, updatedMenu, image);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error updating menu", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating menu: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        boolean deleted = menuService.deleteMenu(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> getMenusByRestaurant(@PathVariable Long restaurantId) {
        try {
            List<Menu> menus = menuService.getMenusByRestaurant(restaurantId);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des menus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @GetMapping("/restaurants/{restaurantId}/menus/{menuId}")
    public ResponseEntity<?> getMenuById(
            @PathVariable Long restaurantId,
            @PathVariable Long menuId) {
        try {
            Optional<Menu> menu = menuService.getMenuById(restaurantId, menuId);
            return menu.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du menu", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @GetMapping("/allergens/{menuId}")
    public ResponseEntity<?> detectAllergens(@PathVariable Long menuId) {
        try {
            Optional<Menu> menu = menuRepository.findById(menuId);
            if (menu.isPresent()) {
                List<String> allergens = menuService.detectAllergens(menu.get().getIngredientsDetails());
                return ResponseEntity.ok(allergens);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur lors de la détection des allergènes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @GetMapping("/allergens/ai/{menuId}")
    public ResponseEntity<?> detectAllergensWithAI(@PathVariable Long menuId) {
        try {
            Optional<Menu> menu = menuRepository.findById(menuId);
            if (menu.isPresent()) {
                List<String> allergens = menuService.detectAllergensWithAI(menu.get().getIngredientsDetails());
                return ResponseEntity.ok(allergens);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur lors de la détection des allergènes avec IA", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @PostMapping("/restaurant/{restaurantId}/filter-allergen")
    public ResponseEntity<?> filterMenusByAllergen(
            @PathVariable Long restaurantId,
            @RequestBody Map<String, String> request) {
        try {
            String allergen = request.get("allergen");
            if (allergen == null || allergen.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Allergène requis.");
            }
            List<Menu> menus = menuService.filterMenusByAllergen(restaurantId, allergen.trim());
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            log.error("Erreur lors du filtrage des menus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du filtrage des menus: " + e.getMessage());
        }
    }

    @GetMapping("/uploads/menus/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path filePath = Paths.get("./uploads/menus/", imageName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/chatbot")
    public ResponseEntity<?> handleChatbotQuestion(
            @RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");
            Long restaurantId = Long.parseLong(request.get("restaurantId"));

            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("La question est requise.");
            }

            String response = menuService.answerMenuQuestion(restaurantId, question);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            log.error("Erreur lors du traitement de la question du chatbot", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur: " + e.getMessage());
        }
    }
}