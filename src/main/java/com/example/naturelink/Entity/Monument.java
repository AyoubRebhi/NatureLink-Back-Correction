package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "monument")
public class Monument {

    private static final List<String> SUPPORTED_3D_FORMATS = Arrays.asList("GLTF", "GLB", "OBJ");

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

    // Champs pour le modèle 3D
    private String model3DUrl; // URL du modèle 3D (ex. hébergé sur S3 ou local)
    private String model3DFormat; // Format du modèle (ex. GLTF, OBJ)
    private String model3DStatus; // Statut (ex. "ready", "processing", "failed")
    @Column(name = "model_scale")
    private String modelScale; // Échelle pour VR (ex. "1.0")
    @Column(name = "model_position")
    private String modelPosition; // Position initiale pour VR (ex. "0,0,0")

    @OneToMany(mappedBy = "monument")
    @JsonManagedReference(value = "monument-visit")
    @JsonIgnore
    private List<Visit> visits;

    // Constructeur par défaut
    public Monument() {
        this.modelScale = "1.0"; // Valeur par défaut
        this.modelPosition = "0,0,0"; // Valeur par défaut
    }

    // Constructeur complet
    public Monument(Integer id, String nom, String description, String localisation, String horairesOuverture,
                    float prixEntree, String image, String model3DUrl, String model3DFormat, String model3DStatus,
                    String modelScale, String modelPosition, List<Visit> visits) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.localisation = localisation;
        this.horairesOuverture = horairesOuverture;
        this.prixEntree = prixEntree;
        this.image = image;
        this.model3DUrl = model3DUrl;
        this.model3DFormat = model3DFormat;
        this.model3DStatus = model3DStatus;
        this.modelScale = modelScale != null ? modelScale : "1.0";
        this.modelPosition = modelPosition != null ? modelPosition : "0,0,0";
        this.visits = visits;
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

    public String getModel3DUrl() {
        return model3DUrl;
    }

    public void setModel3DUrl(String model3DUrl) {
        this.model3DUrl = model3DUrl;
    }

    public String getModel3DFormat() {
        return model3DFormat;
    }

    public void setModel3DFormat(String model3DFormat) {
        if (model3DFormat != null && !SUPPORTED_3D_FORMATS.contains(model3DFormat.toUpperCase())) {
            throw new IllegalArgumentException("Unsupported 3D model format: " + model3DFormat);
        }
        this.model3DFormat = model3DFormat;
    }

    public String getModel3DStatus() {
        return model3DStatus;
    }

    public void setModel3DStatus(String model3DStatus) {
        this.model3DStatus = model3DStatus;
    }

    public String getModelScale() {
        return modelScale;
    }

    public void setModelScale(String modelScale) {
        this.modelScale = modelScale != null ? modelScale : "1.0";
    }

    public String getModelPosition() {
        return modelPosition;
    }

    public void setModelPosition(String modelPosition) {
        this.modelPosition = modelPosition != null ? modelPosition : "0,0,0";
    }

    @JsonIgnore
    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
}