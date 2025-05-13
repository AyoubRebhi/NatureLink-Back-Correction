package com.example.naturelink.Controller;

import com.example.naturelink.Entity.AccountStatus;
import com.example.naturelink.Entity.PendingUser;
import com.example.naturelink.Entity.Role;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Service.IUserService;
import com.example.naturelink.Service.EmailService;
import com.example.naturelink.Repository.PendingUserRepository;
import com.example.naturelink.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final PendingUserRepository pendingUserRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ✅ Use injected value instead of hardcoded URL
    @Value("${frontend.url}")
    private String frontendUrl;

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsersAdmin() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PendingUser>> getPendingUsers() {
        List<PendingUser> pendingUsers = pendingUserRepository.findAll();
        return ResponseEntity.ok(pendingUsers);
    }

    @PostMapping("/admin/approve/{pendingUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> approveUser(@PathVariable Integer pendingUserId) {
        PendingUser pendingUser = pendingUserRepository.findById(pendingUserId)
                .orElseThrow(() -> new IllegalArgumentException("Pending user not found"));

        User user = User.builder()
                .username(pendingUser.getUsername())
                .email(pendingUser.getEmail())
                .password(pendingUser.getPassword())
                .role(pendingUser.getRole())
                .status(AccountStatus.ACTIVE)
                .enabled(true)
                .isApproved(true)
                .blocked(false)
                .build();

        user = userRepository.save(user);
        pendingUserRepository.delete(pendingUser);

        emailService.sendAccountApprovedEmail(user.getEmail(), user.getUsername());

        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/approve", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> approveUserByToken(@RequestParam("token") String token) {
        PendingUser pendingUser = pendingUserRepository.findByApprovalToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired approval token"));

        if (pendingUser.getExpiryDate() == null || pendingUser.getExpiryDate().isBefore(LocalDateTime.now())) {
            pendingUserRepository.delete(pendingUser);
            throw new IllegalArgumentException("Approval token has expired");
        }

        User user = User.builder()
                .username(pendingUser.getUsername())
                .email(pendingUser.getEmail())
                .password(pendingUser.getPassword())
                .role(pendingUser.getRole())
                .status(AccountStatus.ACTIVE)
                .enabled(true)
                .isApproved(true)
                .blocked(false)
                .build();

        userRepository.save(user);
        pendingUserRepository.delete(pendingUser);

        emailService.sendAccountApprovedEmail(user.getEmail(), user.getUsername());

        return ResponseEntity.ok(
                "<html><body><h2>User Approved Successfully</h2>" +
                        "<p>The user " + user.getUsername() + " has been approved.</p>" +
                        "<a href='" + frontendUrl + "'>Return to Application</a></body></html>"
        );
    }

    @DeleteMapping("/admin/reject/{pendingUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectUser(@PathVariable Integer pendingUserId) {
        PendingUser pendingUser = pendingUserRepository.findById(pendingUserId)
                .orElseThrow(() -> new IllegalArgumentException("Pending user not found"));
        pendingUserRepository.delete(pendingUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        try {
            User updatedUser = userService.updateUser(id, updates);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> blockUser(@PathVariable Integer id) {
        User blockedUser = userService.blockUser(id);
        return ResponseEntity.ok(blockedUser);
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unblockUser(@PathVariable Integer id) {
        User unblockedUser = userService.unblockUser(id);
        return ResponseEntity.ok(unblockedUser);
    }

    @PostMapping("/{id}/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = String.format("%d_%d_%s",
                    id,
                    System.currentTimeMillis(),
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                            .replace(" ", "_")
                            .replace(",", "")
            );

            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            User updatedUser = userService.updateProfilePic(id, fileName);

            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File storage error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
        }
    }
}
