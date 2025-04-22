package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Repository.MonumentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class MonumentServiceImpl implements IMonumentService {

    private final MonumentRepository monumentRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MonumentServiceImpl.class);
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public MonumentServiceImpl(MonumentRepository monumentRepository, RestTemplate restTemplate) {
        this.monumentRepository = monumentRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Monument addMonument(Monument monument) {
        return monumentRepository.save(monument);
    }

    @Override
    public Optional<Monument> getMonumentById(Integer id) {
        return monumentRepository.findById(id);
    }

    @Override
    public List<Monument> getAllMonuments() {
        return monumentRepository.findAll();
    }

    @Override
    public Optional<Monument> updateMonument(Integer id, Monument updatedMonument) {
        return monumentRepository.findById(id).map(existingMonument -> {
            if (updatedMonument.getName() != null) {
                existingMonument.setName(updatedMonument.getName());
            }
            if (updatedMonument.getDescription() != null) {
                existingMonument.setDescription(updatedMonument.getDescription());
            }
            if (updatedMonument.getLocation() != null) {
                existingMonument.setLocation(updatedMonument.getLocation());
            }
            if (updatedMonument.getOpeningHours() != null) {
                existingMonument.setOpeningHours(updatedMonument.getOpeningHours());
            }
            if (updatedMonument.getEntranceFee() != null) {
                existingMonument.setEntranceFee(updatedMonument.getEntranceFee());
            }
            if (updatedMonument.getImage() != null) {
                existingMonument.setImage(updatedMonument.getImage());
            }
            if (updatedMonument.getMapEmbedUrl() != null) {
                existingMonument.setMapEmbedUrl(updatedMonument.getMapEmbedUrl());
            }
            return monumentRepository.save(existingMonument);
        });
    }

    @Override
    @Transactional
    public boolean deleteMonument(Integer id) {
        return monumentRepository.findById(id)
                .map(monument -> {
                    if (monument.getImage() != null && !monument.getImage().isEmpty()) {
                        try {
                            Path imagePath = Paths.get(uploadDir, monument.getImage());
                            Files.deleteIfExists(imagePath);
                        } catch (IOException e) {
                            System.err.println("Failed to delete image file: " + e.getMessage());
                        }
                    }
                    monumentRepository.delete(monument);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Monument enrichMonumentData(String monumentName) throws Exception {
        Optional<Monument> existingMonument = monumentRepository.findByName(monumentName);
        Monument monument = existingMonument.orElseGet(Monument::new);
        monument.setName(monumentName);

        ObjectMapper mapper = new ObjectMapper();

        // 1. Get location from Google Maps
        String encodedName = UriUtils.encode(monumentName, "UTF-8");
        String mapsUrl = String.format(
                "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&fields=formatted_address,geometry&key=%s",
                encodedName, googleMapsApiKey);

        try {
            ResponseEntity<String> mapsResponse = restTemplate.exchange(mapsUrl, HttpMethod.GET, null, String.class);
            JsonNode mapsData = mapper.readTree(mapsResponse.getBody());

            JsonNode candidates = mapsData.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                String location = firstCandidate.path("formatted_address").asText();
                monument.setLocation(location);

                JsonNode geometry = firstCandidate.path("geometry");
                JsonNode locationNode = geometry.path("location");
                double lat = locationNode.path("lat").asDouble();
                double lng = locationNode.path("lng").asDouble();
                String mapEmbedUrl = String.format("https://www.google.com/maps/embed/v1/place?key=%s&q=%f,%f",
                        googleMapsApiKey, lat, lng);
                monument.setMapEmbedUrl(mapEmbedUrl);
            } else {
                monument.setLocation("Location not found.");
            }
        } catch (Exception e) {
            logger.error("Error fetching location data for monument: " + monumentName, e);
            monument.setLocation("Error fetching location data");
        }

        // 2. Generate description from Gemini AI
        String prompt = String.format(
                "Write a concise 150-200 word description of '%s'. Include: " +
                        "1. Historical context " +
                        "2. Architectural features " +
                        "3. Cultural significance " +
                        "4. Current status/use",
                monumentName);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiApiKey);

            String requestBody = String.format("""
        {
          "contents": [
            {
              "parts": [
                {"text": "%s"}
              ]
            }
          ],
          "generationConfig": {
            "maxOutputTokens": 1000
          }
        }
        """, prompt.replace("\"", "\\\""));

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> geminiResponse = restTemplate.exchange(
                    geminiApiUrl, HttpMethod.POST, request, String.class);

            JsonNode geminiData = mapper.readTree(geminiResponse.getBody());
            JsonNode candidatesNode = geminiData.path("candidates");

            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode contentNode = candidatesNode.get(0).path("content");
                if (contentNode.has("parts")) {
                    JsonNode partsNode = contentNode.path("parts");
                    if (partsNode.isArray() && partsNode.size() > 0) {
                        String description = partsNode.get(0).path("text").asText();
                        // Truncate description if needed (max 4000 chars for VARCHAR)
                        if (description.length() > 4000) {
                            description = description.substring(0, 4000);
                            logger.warn("Truncated description for monument: " + monumentName);
                        }
                        monument.setDescription(description);
                    }
                }
            } else {
                monument.setDescription("No description could be generated at this time.");
            }
        } catch (Exception e) {
            logger.error("Error generating description for monument: " + monumentName, e);
            monument.setDescription("Error generating description");
        }

        try {
            return monumentRepository.save(monument);
        } catch (DataAccessException e) {
            logger.error("Error saving monument data: " + e.getMessage());
            throw new Exception("Failed to save monument data", e);
        }
    }
}