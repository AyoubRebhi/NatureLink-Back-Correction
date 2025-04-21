package com.example.naturelink.Service;

import com.example.naturelink.dto.AuthResponse;
import com.example.naturelink.dto.SignInRequest;
import com.example.naturelink.dto.SignUpRequest;
import com.example.naturelink.Entity.AccountStatus;
import com.example.naturelink.Entity.PendingUser;
import com.example.naturelink.Entity.Role;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Exceptions.DuplicateEntityException;
import com.example.naturelink.Repository.PendingUserRepository;
import com.example.naturelink.Repository.UserRepository;
import com.example.naturelink.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername()) || pendingUserRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateEntityException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail()) || pendingUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException("Email already exists");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        if (role == Role.USER) {
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .status(AccountStatus.ACTIVE)
                    .enabled(true)
                    .isApproved(true)
                    .build();
            userRepository.save(user);
            return new AuthResponse(jwtUtils.generateToken(user));
        } else {
            PendingUser pendingUser = PendingUser.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .proofDocument(request.getProofDocument())
                    .build();
            pendingUser = pendingUserRepository.save(pendingUser);

            emailService.sendApprovalRequestEmail(
                    "atef.jarray@esprit.tn",
                    request.getUsername(),
                    role.name(),
                    request.getProofDocument(),
                    pendingUser
            );

            return new AuthResponse("Registration request submitted for approval", true);
        }
    }

    public AuthResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return new AuthResponse(jwtUtils.generateToken(user));
    }
}