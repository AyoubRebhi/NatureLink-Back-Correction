package tn.esprit.azizmehdi.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tn.esprit.azizmehdi.entites.Profession;
import tn.esprit.azizmehdi.entites.Programme;
import tn.esprit.azizmehdi.entites.Thematique;
import tn.esprit.azizmehdi.entites.Utilisateur;
import tn.esprit.azizmehdi.services.TVService;

import java.util.*;

@RestController
@RequestMapping("/api")
public class TVController {

    @Autowired
    private TVService tvService;

    @PostMapping("/utilisateur")
    public Utilisateur ajouterUtilisateur(@RequestBody Utilisateur u) {
        System.out.println("Utilisateur reçu: " + u.getUsrNom());
        return tvService.ajouterUtilisateur(u);
    }

    @PostMapping("/programme-chaine")
    public Programme ajouterProgrammeEtChaine(@RequestBody Programme p) {
        return tvService.ajouterProgrammeEtChaine(p);
    }
    @PostMapping("/programme/{chId}")
    public Programme ajouterProgrammeEtAffecterChaine(@RequestBody Programme p, @PathVariable Long chId) {
        return tvService.ajouterProgrammeEtAffecterChaine(p, chId);
    }

    @PutMapping("/favoris")
    public void affecterProgrammeAUtilisateur(@RequestParam String prNom, @RequestParam String usrNom) {
        tvService.affecterProgrammeAUtilisateur(prNom, usrNom);
    }

    @PutMapping("/desaffecter")
    public void desaffecterProgrammeDeUtilisateur(@RequestParam String prNom, @RequestParam String usrNom) {
        tvService.desaffecterProgrammeDeUtilisateur(prNom, usrNom);
    }

    @GetMapping("/utilisateurs")
    public List<Utilisateur> recupererUtilisateurs(@RequestParam Profession p,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date d,
                                                   @RequestParam Thematique t) {
        return tvService.recupererUtilisateurs(p, d, t);
    }
}
