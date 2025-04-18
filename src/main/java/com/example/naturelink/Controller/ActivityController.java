package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Activity;
import com.example.naturelink.Service.ActivityService;
import com.example.naturelink.Service.GroqService;
import com.example.naturelink.Service.IActivityService;
import com.example.naturelink.dto.RecommendationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.naturelink.dto.ActivityDTO;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@CrossOrigin("*") // Optional: allow frontend access
@RequestMapping("/activities")
public class ActivityController {

    private final IActivityService activityService;
    private final com.example.naturelink.Service.ActivityService activityServiceImpl;

    private final com.example.naturelink.Service.GroqService groqService;
    public ActivityController(IActivityService activityService, ActivityService activityServiceImpl, GroqService groqService) {
        this.activityService = activityService;
        this.activityServiceImpl = activityServiceImpl;
        this.groqService = groqService;
    }
    @PostMapping("/get-activity-images")
    public ResponseEntity<?> getActivityImages(@RequestBody Map<String, String> body) {
        try {
            String description = body.get("description");
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Description is required"));
            }

            List<String> images = groqService.getActivityImages(description);
            return ResponseEntity.ok(Map.of("images", images));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to process request",
                    "details", e.getMessage()
            ));
        }
    }
    @PostMapping("/generate")
    public ResponseEntity<String> generateActivity(@RequestBody Map<String, String> promptParams) {
        try {
            String response = groqService.generateActivityWithPrompt(promptParams);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate activity: " + e.getMessage());
        }
    }

    // Get all activities
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    // Get activity by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable Integer id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        return activity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((Activity) Map.of("message", "Transport not found")));
    }

    // Add new activity

    // Update an activity

    // Delete an activity
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Integer id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok("{\"message\": \"Activity deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Activity not found\"}");
        }
    }
    @PostMapping(value = "/add-images", consumes = {"multipart/form-data"})
    public ResponseEntity<Activity> addActivityWithImages(
            @RequestPart("activity") Activity activity,
            @RequestPart("images") List<MultipartFile> imageFiles) {

        try {
            Activity saved = activityServiceImpl.addActivityWithImages(activity, imageFiles);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @PutMapping(value = "/update-images/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Activity> updateActivityWithImages(
            @PathVariable Integer id,
            @RequestPart("activity") Activity activity,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {

        try {
            Activity updated = activityServiceImpl.updateActivityWithImages(id, activity, imageFiles);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Recommandation Controller
    @PostMapping("/recommend")
    public ResponseEntity<?> recommendActivities(@RequestBody RecommendationRequest request) {
        try {
            String moodInput = request.getMood_input();
            List<ActivityDTO> dtoList = request.getActivities();

            WebClient client = WebClient.builder()
                    .baseUrl("http://localhost:5005")
                    .build();

            String responseJson = client.post()
                    .uri("/recommend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("mood_input", moodInput, "activities", dtoList))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

// Parse response
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> recommended = mapper.readValue(responseJson, new TypeReference<>() {});

// Map original activity imageUrls by name
            Map<String, List<String>> nameToImages = activityService.getAllActivities().stream()
                    .collect(Collectors.toMap(Activity::getName, Activity::getImageUrls));

// Inject imageUrls back
            for (Map<String, Object> act : recommended) {
                String name = (String) act.get("name");
                act.put("imageUrls", nameToImages.getOrDefault(name, List.of()));
            }

            return ResponseEntity.ok(recommended);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Recommendation engine error: " + e.getMessage());
        }
    }
}
