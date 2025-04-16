package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Visit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    @Transactional
    void deleteAllByMonumentId(Integer monumentId);

    @Query("SELECT v FROM Visit v LEFT JOIN FETCH v.monument LEFT JOIN FETCH v.guide WHERE v.monument.id = :monumentId")
    List<Visit> findByMonumentIdWithRelations(@Param("monumentId") Integer monumentId);

    @Query("SELECT v FROM Visit v LEFT JOIN FETCH v.monument LEFT JOIN FETCH v.guide")
    List<Visit> findAllWithRelations();

    List<Visit> findByMonumentId(Integer monumentId);
}