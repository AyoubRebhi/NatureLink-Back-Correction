package com.example.naturelink.service;

import com.example.naturelink.entity.Boutique;
import com.example.naturelink.entity.Produit;
import com.example.naturelink.repository.IBoutiqueRepository;
import com.example.naturelink.repository.IProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoutiqueService {
    @Autowired
    private IBoutiqueRepository boutiqueRepository;

    @Autowired
    private  IProduitRepository produitRepository;

    public Boutique createBoutique(Boutique boutique) {
        return boutiqueRepository.save(boutique);
    }

    public Produit addProduitToBoutique(Long boutiqueId, Produit produit) {
        Boutique boutique = boutiqueRepository.findById(boutiqueId)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        produit.setBoutique(boutique);
        return produitRepository.save(produit);
    }

    public List<Produit> getProduitsByBoutique(Long boutiqueId) {
        Boutique boutique = boutiqueRepository.findById(boutiqueId)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        return boutique.getProduits();
    }

    public List<Boutique> getAllBoutiques() {
        return boutiqueRepository.findAll();
    }
}
