package com.example.naturelink.dto;

import java.time.LocalDate;

public class DisponibilityRequestDto {

    private Integer logementId;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters and setters
    public Integer getLogementId() {
        return logementId;
    }

    public void setLogementId(Integer logementId) {
        this.logementId = logementId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
