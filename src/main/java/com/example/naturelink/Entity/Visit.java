package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Visit")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    private String time;
    private float price;
    private String duration;
    private String nomGuide;
    private String nomMonument;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "monument_id")
    @JsonBackReference(value = "monument-visit")
    private Monument monument;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guide_id")
    @JsonBackReference(value = "guide-visit")
    private TourGuide guide;

    public Visit() {
    }

    public Visit(Integer id, LocalDate date, String time, float price, String duration,
                 Monument monument, TourGuide guide) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.price = price;
        this.duration = duration;
        this.monument = monument;
        this.guide = guide;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Monument getMonument() {
        return monument;
    }

    public void setMonument(Monument monument) {
        this.monument = monument;
        if (monument != null) {
            this.nomMonument = monument.getNom();
        }
    }

    public TourGuide getGuide() {
        return guide;
    }

    public void setGuide(TourGuide guide) {
        this.guide = guide;
        if (guide != null) {
            String firstName = guide.getFirstName() != null ? guide.getFirstName() : "";
            String lastName = guide.getLastName() != null ? guide.getLastName() : "";
            this.nomGuide = (firstName + " " + lastName).trim();
        }
    }

    public String getNomGuide() {
        return nomGuide;
    }

    public void setNomGuide(String nomGuide) {
        this.nomGuide = nomGuide;
    }

    public String getNomMonument() {
        return nomMonument;
    }

    public void setNomMonument(String nomMonument) {
        this.nomMonument = nomMonument;
    }

    @PrePersist
    @PreUpdate
    private void synchronizeNames() {
        if (monument != null) {
            this.nomMonument = monument.getNom();
        }
        if (guide != null) {
            String firstName = guide.getFirstName() != null ? guide.getFirstName() : "";
            String lastName = guide.getLastName() != null ? guide.getLastName() : "";
            this.nomGuide = (firstName + " " + lastName).trim();
        }
    }
}