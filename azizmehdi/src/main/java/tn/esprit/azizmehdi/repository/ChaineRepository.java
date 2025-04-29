package tn.esprit.azizmehdi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.azizmehdi.entites.Chaine;

import java.util.List;

public interface ChaineRepository extends JpaRepository<Chaine, Long>{
    @Query("SELECT c.chNom, COUNT(p) FROM Programme p JOIN p.utilisateurs u JOIN p.chaine c GROUP BY c.chNom ORDER BY COUNT(p) DESC")
    List<Object[]> countProgrammesFavorisByChaine();

}
