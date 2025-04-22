package com.example.naturelink.Entity;

import jakarta.persistence.*;


@Entity
@Table(name = "monument")
public class Monument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String location;

    @Column(name = "opening_hours", length = 100)
    private String openingHours;

    @Column(name = "prix_entree", nullable = true)
    private Float entranceFee;

    @Column(length = 255)
    private String image;

    @Column(name = "map_embed_url", length = 500)
    private String mapEmbedUrl;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    // Modifié pour utiliser Float
    public Float getEntranceFee() { return entranceFee; }
    public void setEntranceFee(Float entranceFee) { this.entranceFee = entranceFee; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getMapEmbedUrl() { return mapEmbedUrl; }
    public void setMapEmbedUrl(String mapEmbedUrl) { this.mapEmbedUrl = mapEmbedUrl; }
}