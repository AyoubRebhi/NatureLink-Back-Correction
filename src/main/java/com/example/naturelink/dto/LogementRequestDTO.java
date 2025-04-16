package com.example.naturelink.dto;

import com.example.naturelink.Entity.LogementType;

import java.util.List;

public class LogementRequestDTO {

    private String titre;
    private String description;
    private String location;
    private LogementType type;  // Enum for type
    private Double price;
    private String image;
    private Integer proprietarield;
    private String phone;
    private String email;
    private String socialMedia;
    private Integer capacity; // The new attribute

    private Integer singleRooms;
    private Integer doubleRooms;
    private List<Integer> equipementIds;
    private List<String> newEquipements;
    private List<String> images; // List of image URLs or paths

    // Getters and setters for all fields

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

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
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LogementType getType() {
        return type;
    }

    public void setType(LogementType type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

    public Integer getSingleRooms() {
        return singleRooms;
    }

    public void setSingleRooms(Integer singleRooms) {
        this.singleRooms = singleRooms;
    }

    public Integer getDoubleRooms() {
        return doubleRooms;
    }

    public void setDoubleRooms(Integer doubleRooms) {
        this.doubleRooms = doubleRooms;
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
