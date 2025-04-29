package tn.esprit.azizmehdi.services;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.azizmehdi.entites.Utilisateur;
import tn.esprit.azizmehdi.repository.ChaineRepository;
import tn.esprit.azizmehdi.repository.ProgrammeRepository;
import tn.esprit.azizmehdi.repository.UtilisateurRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private ProgrammeRepository programmeRepo;

    @Autowired
    private ChaineRepository chaineRepo;

    public Utilisateur ajouterUtilisateur(Utilisateur u) {
        return utilisateurRepo.save(u);
    }

}
