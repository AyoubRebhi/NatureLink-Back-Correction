package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "tour_guide")
public class TourGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("firstName")
    @Column(nullable = false)
    private String firstName;

    @JsonProperty("lastName")
    @Column(nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Visit> visits;
}