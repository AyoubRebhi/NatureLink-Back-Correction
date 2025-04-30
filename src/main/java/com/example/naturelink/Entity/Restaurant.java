package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String localisation;
    private String typeCuisine;
    private String horairesOuverture;
    private String image;
    @Column(nullable = false)
    private Integer capacite;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore

    private List<Menu> menus;

    // Getters and setters
    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }
    public Integer getCapacite() {
        return capacite;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getTypeCuisine() {
        return typeCuisine;
    }

    public void setTypeCuisine(String typeCuisine) {
        this.typeCuisine = typeCuisine;
    }

    public String getHorairesOuverture() {
        return horairesOuverture;
    }

    public void setHorairesOuverture(String horairesOuverture) {
        this.horairesOuverture = horairesOuverture;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
