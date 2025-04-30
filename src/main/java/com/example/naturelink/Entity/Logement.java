package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Logement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titre;
    private String description;
    private String location;

    @Enumerated(EnumType.STRING)
    private LogementType type;

    private Double price;
    private Integer proprietarield;
    private String phone;
    private String email;
    private String socialMedia;
    private Integer capacity; // The new attribute

    // House specific
    private Integer singleRooms;
    private Integer doubleRooms;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images = new ArrayList<>();

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // Change this to use the Equipement enum
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "logement_equipements",
            joinColumns = @JoinColumn(name = "logement_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id")
    )


    private List<Equipement> equipements = new ArrayList<>();
    // Getters and Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LogementType getType() { return type; }
    public void setType(LogementType type) { this.type = type; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getProprietarield() { return proprietarield; }
    public void setProprietarield(Integer proprietarield) { this.proprietarield = proprietarield; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSocialMedia() { return socialMedia; }
    public void setSocialMedia(String socialMedia) { this.socialMedia = socialMedia; }

    public Integer getSingleRooms() { return singleRooms; }
    public void setSingleRooms(Integer singleRooms) { this.singleRooms = singleRooms; }

    public Integer getDoubleRooms() { return doubleRooms; }
    public void setDoubleRooms(Integer doubleRooms) { this.doubleRooms = doubleRooms; }

    public List<Equipement> getEquipements() { return equipements; }
    public void setEquipements(List<Equipement> equipements) { this.equipements = equipements; }
}
