package com.example.naturelink.Entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plats;
    private float prixMoyen;
    private String image;
    private String ingredientsDetails;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public Menu() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlats() {
        return plats;
    }

    public void setPlats(String plats) {
        this.plats = plats;
    }

    public float getPrixMoyen() {
        return prixMoyen;
    }

    public void setPrixMoyen(float prixMoyen) {
        this.prixMoyen = prixMoyen;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredientsDetails() {
        return ingredientsDetails;
    }

    public void setIngredientsDetails(String ingredientsDetails) {
        this.ingredientsDetails = ingredientsDetails;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
