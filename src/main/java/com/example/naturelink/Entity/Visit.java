package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
//import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

//@Data
@Entity
@Table(name = "visit")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private String duration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "monument_id", nullable = false)
    @JsonBackReference(value = "monument-visit")
    private Monument monument;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guide_id", nullable = false)
    @JsonBackReference(value = "guide-visit")
    private TourGuide guide;

    public String getGuideName() {
        if (guide == null) return null;
        String firstName = guide.getFirstName() != null ? guide.getFirstName() : "";
        String lastName = guide.getLastName() != null ? guide.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }

    public String getMonumentName() {
        return monument != null ? monument.getName() : null;
    }

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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
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
    }

    public TourGuide getGuide() {
        return guide;
    }

    public void setGuide(TourGuide guide) {
        this.guide = guide;
    }
}