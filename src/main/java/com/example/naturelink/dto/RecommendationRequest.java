package com.example.naturelink.dto;

import java.util.List;

public class RecommendationRequest {
    private String mood_input;
    private List<ActivityDTO> activities;

    public String getMood_input() {
        return mood_input;
    }

    public void setMood_input(String mood_input) {
        this.mood_input = mood_input;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}

