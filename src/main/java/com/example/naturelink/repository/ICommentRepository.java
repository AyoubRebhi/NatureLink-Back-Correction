package com.example.naturelink.repository;
import com.example.naturelink.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
    // Des méthodes personnalisées peuvent être ajoutées ici si nécessaire
}
