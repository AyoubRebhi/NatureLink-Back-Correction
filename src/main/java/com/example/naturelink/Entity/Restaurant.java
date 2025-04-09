package com.example.naturelink.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String localisation;

    @Column(nullable = false)
    private String typeCuisine;

    @Column(nullable = false)
    private String horairesOuverture;

    @Column(nullable = false)
    private String image;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus;
    //@ManyToOne
    //@JoinColumn(name = "user_id")
    //private User user;

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


    public Restaurant() {
    }

    public Restaurant(Long id, String nom, String description, String localisation, String typeCuisine,
                      String horairesOuverture, String image, List<Menu> menus) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.localisation = localisation;
        this.typeCuisine = typeCuisine;
        this.horairesOuverture = horairesOuverture;
        this.image = image;
        this.menus = menus;
    }
}
