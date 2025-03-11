package com.example.naturelink.repository;
import com.example.naturelink.Entity.Clothing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface iClothingRepository extends JpaRepository<Clothing, Long> {
    List<Clothing> findByDestinationId(Long destinationId);
}

