package com.example.naturelink.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlaskClientService {

    private static final Logger logger = LoggerFactory.getLogger(FlaskClientService.class);

    @Value("${flask.api.url}")
    private String flaskUrl; // e.g., http://127.0.0.1:5001/validate

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FlaskClientService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public boolean areNamesValid(List<String> names) {
        try {
            // Prepare request
            Map<String, Object> body = new HashMap<>();
            body.put("name", names);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            logger.info("Sending validation request to Flask API: {}", body);

            // Send POST request
            ResponseEntity<String> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Log raw response
            logger.info("Flask API raw response: {}", response.getBody());

            // Parse response
            List<Map<String, String>> results = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<Map<String, String>>>() {}
            );

            // Check for invalid names
            for (Map<String, String> result : results) {
                String name = result.get("name");
                String validationResult = result.get("result");
                logger.info("Name: {}, Result: {}", name, validationResult);
                if ("INVALID".equals(validationResult)) { // Exact match for "INVALID"
                    logger.warn("Invalid name detected: {}", name);
                    return false;
                }
            }

            logger.info("All names valid: {}", names);
            return true;
        } catch (Exception e) {
            logger.error("Error communicating with Flask API: {}", e.getMessage(), e);
            return false; // Fail-safe: deny if something fails
        }
    }
}