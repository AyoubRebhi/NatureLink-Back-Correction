package com.example.naturelink.Repository;


import com.example.naturelink.Entity.Monument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonumentRepository extends JpaRepository<Monument, Integer> {
    @Modifying
    @Query("DELETE FROM Visit v WHERE v.monument.id = :id")
    void deleteAllByMonumentId(@Param("id") Integer id);

}