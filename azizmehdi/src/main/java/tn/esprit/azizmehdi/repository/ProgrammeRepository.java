package tn.esprit.azizmehdi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.azizmehdi.entites.Programme;

import java.util.List;
import java.util.Optional;

public interface ProgrammeRepository extends JpaRepository<Programme,Long> {
    List<Programme> findByPrNom(String prNom);
}
