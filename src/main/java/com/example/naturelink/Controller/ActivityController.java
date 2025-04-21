package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Activity;
import com.example.naturelink.Service.ActivityService;
import com.example.naturelink.Service.GroqService;
import com.example.naturelink.Service.IActivityService;
import com.example.naturelink.dto.RecommendationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.naturelink.dto.ActivityDTO;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
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
            // Validate input
            if (request.getMood_input() == null || request.getMood_input().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Mood input is required",
                        "status", HttpStatus.BAD_REQUEST.value()
                ));
            }

            if (request.getActivities() == null || request.getActivities().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Activities list cannot be empty",
                        "status", HttpStatus.BAD_REQUEST.value()
                ));
            }

            // Prepare request for Python service
            WebClient client = WebClient.builder()
                    .baseUrl("http://localhost:5005")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            // Convert DTOs to proper format for the Python service
            List<Map<String, Object>> activityMaps = request.getActivities().stream()
                    .map(dto -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", dto.getName());
                        map.put("description", dto.getDescription());
                        map.put("type", dto.getType());
                        map.put("mood", String.join(",", dto.getMood() != null ? dto.getMood() : List.of()));
                        map.put("tags", String.join(",", dto.getTags() != null ? dto.getTags() : List.of()));
                        map.put("imageUrls", dto.getImageUrls() != null ? dto.getImageUrls() : List.of());
                        return map;
                    })
                    .collect(Collectors.toList());

            // Call Python recommendation service with timeout
            List<Map<String, Object>> recommended = client.post()
                    .uri("/recommend")
                    .bodyValue(Map.of(
                            "mood_input", request.getMood_input(),
                            "activities", activityMaps
                    ))
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException(
                                        "Recommendation service error: " + error
                                )));
                    })
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .timeout(Duration.ofSeconds(10)) // Add timeout
                    .block();

            // Enrich with images from database
            Map<String, List<String>> nameToImages = activityService.getAllActivities().stream()
                    .collect(Collectors.toMap(
                            Activity::getName,
                            a -> a.getImageUrls() != null ? a.getImageUrls() : List.of()
                    ));

            // Process recommendations
            List<Map<String, Object>> enrichedRecommendations = recommended.stream()
                    .map(rec -> {
                        Map<String, Object> enriched = new HashMap<>(rec);
                        String name = (String) rec.get("name");
                        enriched.put("imageUrls", nameToImages.getOrDefault(name, List.of()));

                        // Add original ID if available
                        request.getActivities().stream()
                                .filter(dto -> dto.getName().equals(name))
                                .findFirst()
                                .ifPresent(dto -> enriched.put("id", dto.getId()));

                        return enriched;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "recommendations", enrichedRecommendations,
                    "status", "success",
                    "model", "BERT" // Indicate which model was used
            ));

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", "Recommendation service error",
                    "details", e.getResponseBodyAsString(),
                    "status", e.getStatusCode().value()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to get recommendations",
                    "details", e.getMessage(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
            ));
        }
    }}
