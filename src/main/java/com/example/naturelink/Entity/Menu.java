package com.example.naturelink.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plats;

    @Column(name = "prix_moyen", nullable = false)
    private float prixMoyen;

    @Column(name = "ingredients_details")
    private String ingredientsDetails;

    private String image;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    // Getters and Setters
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

    public String getIngredientsDetails() {
        return ingredientsDetails;
    }

    public void setIngredientsDetails(String ingredientsDetails) {
        this.ingredientsDetails = ingredientsDetails;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
