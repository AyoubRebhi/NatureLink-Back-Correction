package tn.esprit.azizmehdi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.azizmehdi.entites.Profession;
import tn.esprit.azizmehdi.entites.Thematique;
import tn.esprit.azizmehdi.entites.Utilisateur;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsrNom(String usrNom);
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.programmesFavoris WHERE u.usrNom = :usrNom")
    Optional<Utilisateur> findByUsrNomWithFavoris(@Param("usrNom") String usrNom);


    @Query("SELECT u FROM Utilisateur u JOIN u.programmesFavoris p WHERE u.profession = :p AND u.usrDateInscription > :d AND p.chaine.chTheme = :t")
    List<Utilisateur> filterUtilisateurs(Profession p, Date d, Thematique t);
}
