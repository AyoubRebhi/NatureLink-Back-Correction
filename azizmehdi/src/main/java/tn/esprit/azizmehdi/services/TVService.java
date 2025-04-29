package tn.esprit.azizmehdi.services;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.azizmehdi.entites.*;
import tn.esprit.azizmehdi.repository.ChaineRepository;
import tn.esprit.azizmehdi.repository.ProgrammeRepository;
import tn.esprit.azizmehdi.repository.UtilisateurRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor


public class TVService {
    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private ProgrammeRepository programmeRepo;

    @Autowired
    private ChaineRepository chaineRepo;

    public Utilisateur ajouterUtilisateur(Utilisateur u) {
        return utilisateurRepo.save(u);
    }

    public Programme ajouterProgrammeEtChaine(Programme p) {
        return programmeRepo.save(p);
    }

    public Programme ajouterProgrammeEtAffecterChaine(Programme p, Long chId) {
        Chaine chaine = chaineRepo.findById(chId).orElse(null);
        if (chaine != null) {
            p.setChaine(chaine);
            return programmeRepo.save(p);
        }
        return null;
    }

    public void affecterProgrammeAUtilisateur(String prNom, String usrNom) {
        List<Programme> list = programmeRepo.findByPrNom(prNom);
        Programme p = list.isEmpty() ? null : list.get(0);

        Utilisateur u = utilisateurRepo.findByUsrNomWithFavoris(usrNom).orElse(null);
        if (p != null && u != null) {
            u.getProgrammesFavoris().add(p);
            utilisateurRepo.save(u);
        }
    }

    public void desaffecterProgrammeDeUtilisateur(String prNom, String usrNom) {
        List<Programme> list = programmeRepo.findByPrNom(prNom);
        Programme p = list.isEmpty() ? null : list.get(0);

        Utilisateur u = utilisateurRepo.findByUsrNomWithFavoris(usrNom).orElse(null);
        if (p != null && u != null) {
            u.getProgrammesFavoris().remove(p);
            utilisateurRepo.save(u);
        }
    }

    public List<Utilisateur> recupererUtilisateurs(Profession p, Date d, Thematique t) {
        return utilisateurRepo.filterUtilisateurs(p, d, t);
    }

    @Scheduled(fixedRate = 20000)
    public void ordonnerChaines() {
        List<Object[]> result = chaineRepo.countProgrammesFavorisByChaine();
        for (Object[] row : result) {
            System.out.println("Chaine : " + row[0] + ". Nombre de fois où les programmes de cette Chaine sont marqués comme favoris : " + row[1]);
        }
    }
}
