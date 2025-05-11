package com.example.naturelink.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String proofDocument;

    @Column(unique = true)
    private String approvalToken;

    private LocalDateTime expiryDate;
}