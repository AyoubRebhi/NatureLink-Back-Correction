package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "monument")
public class Monument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom")
    private String nom;
    private String description;
    private String localisation;
    private String horairesOuverture;
    private float prixEntree;
    private String image;

    @OneToMany(mappedBy = "monument")
    @JsonManagedReference(value = "monument-visit")
    private List<Visit> visits;


    // Constructeur par défaut
    public Monument() {}

    // Constructeur complet
    public Monument(Integer id, String nom, String description, String localisation, String horairesOuverture, float prixEntree, String image, List<Visit> visite) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.localisation = localisation;
        this.horairesOuverture = horairesOuverture;
        this.prixEntree = prixEntree;
        this.image = image;

    }


    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getHorairesOuverture() {
        return horairesOuverture;
    }

    public void setHorairesOuverture(String horairesOuverture) {
        this.horairesOuverture = horairesOuverture;
    }

    public float getPrixEntree() {
        return prixEntree;
    }

    public void setPrixEntree(float prixEntree) {
        this.prixEntree = prixEntree;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
