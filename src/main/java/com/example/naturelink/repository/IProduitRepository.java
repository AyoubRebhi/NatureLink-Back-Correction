package com.example.naturelink.repository;

import com.example.naturelink.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProduitRepository extends JpaRepository<Produit, Long> {
}
