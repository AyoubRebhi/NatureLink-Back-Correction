package com.example.naturelink.controller;

import com.example.naturelink.entity.Boutique;
import com.example.naturelink.entity.Produit;
import com.example.naturelink.service.BoutiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/boutiques")
public class BoutiqueController {
    @Autowired
    private BoutiqueService boutiqueService;

    @PostMapping
    public Boutique createBoutique(@RequestBody Boutique boutique) {
        return boutiqueService.createBoutique(boutique);
    }

    @PostMapping("/{id}/produits")
    public Produit addProduit(@PathVariable Long id, @RequestBody Produit produit) {
        return boutiqueService.addProduitToBoutique(id, produit);
    }

    @GetMapping("/{id}/produits")
    public List<Produit> getProduits(@PathVariable Long id) {
        return boutiqueService.getProduitsByBoutique(id);
    }

    @GetMapping
    public List<Boutique> getAll() {
        return boutiqueService.getAllBoutiques();
    }
}
