package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Equipement;
import com.example.naturelink.Service.EquipementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/equipements")
public class EquipementController {

    @Autowired
    private EquipementService equipementService;

    @GetMapping
    public List<Equipement> getAllEquipements() {
        return equipementService.getAllEquipements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipement> getEquipementById(@PathVariable Integer id) {
        Optional<Equipement> equipement = equipementService.getEquipementById(id);
        return equipement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Equipement> createEquipement(@RequestBody Equipement equipement) {
        Equipement createdEquipement = equipementService.createEquipement(equipement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEquipement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipement> updateEquipement(@PathVariable Integer id, @RequestBody Equipement equipementDetails) {
        Equipement updatedEquipement = equipementService.updateEquipement(id, equipementDetails);
        return ResponseEntity.ok(updatedEquipement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipement(@PathVariable Integer id) {
        equipementService.deleteEquipement(id);
        return ResponseEntity.noContent().build();
    }
}
