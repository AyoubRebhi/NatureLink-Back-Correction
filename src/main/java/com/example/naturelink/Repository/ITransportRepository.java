package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITransportRepository extends JpaRepository<Transport, Long> {
    List<Transport> findByAgenceId(Integer agenceId);
    // Define custom query methods if needed
}