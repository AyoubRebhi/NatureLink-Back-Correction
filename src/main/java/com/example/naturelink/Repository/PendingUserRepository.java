package com.example.naturelink.Repository;

import com.example.naturelink.Entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingUserRepository extends JpaRepository<PendingUser, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<PendingUser> findByApprovalToken(String approvalToken);
}