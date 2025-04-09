package com.example.naturelink.Entity;

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

    @ManyToOne
    @JoinColumn(name = "monument_id")
    private Monument monument;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private TourGuide guide;

    //@ManyToOne
    //@JoinColumn(name = "user_id")
//private User user;

    public Visit() {
    }

    public Visit(Integer id, LocalDate date, String time, float price, String duration, Monument monument, TourGuide guide) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.price = price;
        this.duration = duration;
        this.monument = monument;
        this.guide = guide;
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
    }

    public TourGuide getGuide() {
        return guide;
    }

    public void setGuide(TourGuide guide) {
        this.guide = guide;
    }
}
