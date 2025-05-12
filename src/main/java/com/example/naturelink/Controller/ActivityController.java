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
@CrossOrigin(origins = "*") // Optional: for frontend testing
@RequestMapping("/api/activities")
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

            // Convert activities to format expected by Python service
            List<Map<String, Object>> activityMaps = request.getActivities().stream()
                    .map(activity -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", activity.getId());
                        map.put("name", activity.getName());
                        map.put("description", activity.getDescription());
                        map.put("type", activity.getType());
                        map.put("mood", activity.getMood() != null ? activity.getMood() : List.of());
                        map.put("tags", activity.getTags() != null ? activity.getTags() : List.of());
                        map.put("imageUrls", activity.getImageUrls() != null ? activity.getImageUrls() : List.of());
                        map.put("requiredEquipment", activity.getRequiredEquipment() != null ?
                                activity.getRequiredEquipment() : List.of());
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> payload = Map.of(
                    "mood_input", request.getMood_input(),
                    "activities", activityMaps
            );

            // Log the outgoing payload for debugging
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("Sending to Python service: " + mapper.writeValueAsString(payload));

            // Call Python recommendation service
            Map<String, Object> pythonResponse = client.post()
                    .uri("/recommend")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(30))
                    .block();

            // Log the incoming response for debugging
            System.out.println("Received from Python service: " + mapper.writeValueAsString(pythonResponse));

            // Verify response structure
            if (pythonResponse == null || !"success".equals(pythonResponse.get("status"))) {
                String error = pythonResponse != null ?
                        (String) pythonResponse.getOrDefault("error", "Unknown error") : "Null response";
                throw new RuntimeException("Python service error: " + error);
            }

            // Extract recommendations
            List<Map<String, Object>> recommendations = (List<Map<String, Object>>) pythonResponse.get("recommendations");
            String model = (String) pythonResponse.getOrDefault("model", "BERT");

            // Enrich recommendations with additional data if needed
            List<Map<String, Object>> enrichedRecommendations = recommendations.stream()
                    .map(rec -> {
                        Map<String, Object> enriched = new HashMap<>(rec);
                        // Add any additional enrichment here if needed
                        return enriched;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "recommendations", enrichedRecommendations,
                    "status", "success",
                    "model", model
            ));

        } catch (WebClientResponseException e) {
            System.err.println("WebClient error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", "Recommendation service error",
                    "details", e.getResponseBodyAsString(),
                    "status", e.getStatusCode().value()
            ));
        } catch (Exception e) {
            System.err.println("Recommendation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to get recommendations",
                    "details", e.getMessage(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
            ));
        }
    }
  }
