package com.example.naturelink.Service;

import com.example.naturelink.Entity.Menu;
import com.example.naturelink.Repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService implements IMenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${menu.images.directory}")
    private String uploadDir;

    @Override
    public Menu createMenu(Menu menu, Long restaurantId, MultipartFile image) throws Exception {
        menu.setRestaurantId(restaurantId);
        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            menu.setImage(imagePath);
        }
        return menuRepository.save(menu);
    }

    @Override
    public Menu updateMenu(Long id, Menu menu, MultipartFile image) throws Exception {
        Optional<Menu> existingMenu = menuRepository.findById(id);
        if (existingMenu.isPresent()) {
            Menu updatedMenu = existingMenu.get();
            updatedMenu.setPlats(menu.getPlats());
            updatedMenu.setPrixMoyen(menu.getPrixMoyen());
            updatedMenu.setIngredientsDetails(menu.getIngredientsDetails());
            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);
                updatedMenu.setImage(imagePath);
            }
            return menuRepository.save(updatedMenu);
        }
        throw new Exception("Menu non trouvé avec l'ID: " + id);
    }

    @Override
    public boolean deleteMenu(Long id) {
        if (menuRepository.existsById(id)) {
            menuRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Menu> getMenusByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public Optional<Menu> getMenuById(Long restaurantId, Long menuId) {
        return menuRepository.findByIdAndRestaurantId(menuId, restaurantId);
    }

    @Override
    public List<String> detectAllergens(String ingredientsDetails) {
        List<String> commonAllergens = Arrays.asList("gluten", "lait", "oeuf", "arachide", "soja", "noix", "poisson", "crustacé");
        return commonAllergens.stream()
                .filter(allergen -> ingredientsDetails.toLowerCase().contains(allergen.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> detectAllergensWithAI(String ingredientsDetails) {
        try {
            String prompt = "Identifiez les allergènes dans la liste suivante d'ingrédients : " + ingredientsDetails +
                    "\nRetournez une liste d'allergènes détectés (ex. gluten, lait, arachide).";
            return callGeminiApiForAllergens(prompt);
        } catch (Exception e) {
            log.error("Erreur lors de la détection des allergènes avec IA", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Menu> filterMenusByAllergen(Long restaurantId, String allergen) throws Exception {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        List<Menu> filteredMenus = new ArrayList<>();

        for (Menu menu : menus) {
            String ingredients = menu.getIngredientsDetails();
            if (ingredients == null || ingredients.trim().isEmpty()) {
                continue;
            }

            String prompt = "Ingrédients : " + ingredients +
                    "\nContient l'allergène suivant : " + allergen + " ? Répondez uniquement par 'Oui' ou 'Non'.";

            try {
                String response = callGeminiApi(prompt);
                if (response.trim().equalsIgnoreCase("Non")) {
                    filteredMenus.add(menu);
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'appel à l'API Gemini pour le menu ID {}: {}", menu.getId(), e.getMessage());
            }
        }

        return filteredMenus;
    }

    private String callGeminiApi(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", prompt)
                ))
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;
        ResponseEntity<Map> response = restTemplate.postForEntity(urlWithKey, request, Map.class);

        if (response.getBody() == null) {
            throw new Exception("Réponse vide de l'API Gemini");
        }

        Map<String, Object> responseBody = response.getBody();
        List<?> candidates = (List<?>) responseBody.get("candidates");
        if (candidates.isEmpty()) {
            throw new Exception("Aucun candidat trouvé dans la réponse de l'API Gemini");
        }

        Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
        List<?> parts = (List<?>) content.get("parts");
        Map<?, ?> part = (Map<?, ?>) parts.get(0);
        return (String) part.get("text");
    }

    private List<String> callGeminiApiForAllergens(String prompt) throws Exception {
        String response = callGeminiApi(prompt);
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String answerMenuQuestion(Long restaurantId, String question) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        StringBuilder menuSummary = new StringBuilder("Voici les menus disponibles :\n");
        for (Menu menu : menus) {
            menuSummary.append("- ").append(menu.getPlats())
                    .append(" (").append(menu.getPrixMoyen()).append("€, Ingrédients: ")
                    .append(menu.getIngredientsDetails()).append(")\n");
        }

        String prompt = "Question de l'utilisateur : " + question + "\n" +
                "Informations sur les menus :\n" + menuSummary +
                "Répondez à la question en utilisant les informations fournies.";

        try {
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Erreur lors de la réponse à la question avec Gemini", e);
            return "Désolé, une erreur s'est produite. Veuillez réessayer.";
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());
        return fileName;
    }
}