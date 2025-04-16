package com.example.naturelink.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "TourGuide")
public class TourGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "guide")
    @JsonManagedReference(value = "guide-visit")
    private List<Visit> visits;



    public TourGuide() {
    }


    public TourGuide(Integer id, String firstName, String lastName, String email, String phone, List<Visit> visits) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.visits = visits;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
}
