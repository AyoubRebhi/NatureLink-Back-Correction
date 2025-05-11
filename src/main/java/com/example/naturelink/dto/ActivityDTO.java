package com.example.naturelink.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDTO {
    private Integer id;
    private String name;
    private String description;
    private String location;
    private Integer duration;
    private Float price;
    private Integer maxParticipants;
    private String difficultyLevel;
    private String type;
    private List<String> mood;
    private List<String> tags;
    private List<String> requiredEquipment;
    private List<String> imageUrls;

    // ✅ Add Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getMood() {
        return mood;
    }

    public void setMood(List<String> mood) {
        this.mood = mood;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonSetter("requiredEquipment")
    public void setRequiredEquipment(Object equipment) {
        if (equipment == null) {
            this.requiredEquipment = List.of();
        } else if (equipment instanceof String) {
            this.requiredEquipment = Arrays.stream(((String) equipment).split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } else if (equipment instanceof List) {
            // Safe type conversion
            this.requiredEquipment = ((List<?>) equipment).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else {
            this.requiredEquipment = List.of();
        }
    }

    public List<String> getRequiredEquipment() {
        return requiredEquipment != null ? requiredEquipment : List.of();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
