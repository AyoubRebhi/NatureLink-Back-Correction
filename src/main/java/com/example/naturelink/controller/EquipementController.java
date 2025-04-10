package com.example.naturelink.controller;

import com.example.naturelink.entity.Equipement;
import com.example.naturelink.service.EquipementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {

    @Autowired
    private EquipementService equipementService;

    @GetMapping
    public List<Equipement> getAllEquipements() {
        return equipementService.getAllEquipements();
    }

    @GetMapping("/{id}")
    public Optional<Equipement> getEquipementById(@PathVariable Integer id) {
        return equipementService.getEquipementById(id);
    }

    @PostMapping
    public Equipement createEquipement(@Valid @RequestBody Equipement equipement) {
        return equipementService.addEquipement(equipement);
    }


    @DeleteMapping("/{id}")
    public void deleteEquipement(@PathVariable Integer id) {
        equipementService.deleteEquipement(id);
    }
}
