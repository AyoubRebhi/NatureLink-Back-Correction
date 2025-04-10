package com.example.naturelink.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class LogementRequestDTO {

    @NotBlank
    private String titre;

    @NotBlank
    private String description;

    private String location; // Add location field
    private double price;
    private String image;
    private Integer proprietarield;
    private String phone;
    private String email;
    private String socialMedia;
    private List<Integer> equipementIds;
    private List<String> newEquipements;

    // Getters and Setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getProprietarield() {
        return proprietarield;
    }

    public void setProprietarield(Integer proprietarield) {
        this.proprietarield = proprietarield;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public List<Integer> getEquipementIds() {
        return equipementIds;
    }

    public void setEquipementIds(List<Integer> equipementIds) {
        this.equipementIds = equipementIds;
    }

    public List<String> getNewEquipements() {
        return newEquipements;
    }

    public void setNewEquipements(List<String> newEquipements) {
        this.newEquipements = newEquipements;
    }
}
