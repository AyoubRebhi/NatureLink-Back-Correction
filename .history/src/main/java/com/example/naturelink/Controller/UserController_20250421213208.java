package com.example.naturelink.Controller;

<<<<<<< HEAD
import com.example.naturelink.Entity.AccountStatus;
import com.example.naturelink.Entity.PendingUser;
import com.example.naturelink.Entity.Role;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Service.IUserService;
import com.example.naturelink.Service.EmailService;
import com.example.naturelink.Repository.PendingUserRepository;
import com.example.naturelink.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.StringUtils;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final PendingUserRepository pendingUserRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final String frontendUrl = "http://localhost:4200"; // Hardcoded frontend URL

    // Fetch all users (for admin dashboard)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsersAdmin() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Fetch all pending users
    @GetMapping("/admin/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PendingUser>> getPendingUsers() {
        List<PendingUser> pendingUsers = pendingUserRepository.findAll();
        return ResponseEntity.ok(pendingUsers);
    }

    // Approve via dashboard
    @PostMapping("/admin/approve/{pendingUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> approveUser(@PathVariable Integer pendingUserId) {
        PendingUser pendingUser = pendingUserRepository.findById(pendingUserId)
                .orElseThrow(() -> new IllegalArgumentException("Pending user not found"));

        // Create User from PendingUser
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

        // Send confirmation email to user
        emailService.sendAccountApprovedEmail(user.getEmail(), user.getUsername());

        return ResponseEntity.ok(user);
    }

    // Approve via email link
    @GetMapping(value = "/approve", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> approveUserByToken(@RequestParam("token") String token) {
        PendingUser pendingUser = pendingUserRepository.findByApprovalToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired approval token"));

        // Check if token is expired
        if (pendingUser.getExpiryDate() == null || pendingUser.getExpiryDate().isBefore(LocalDateTime.now())) {
            pendingUserRepository.delete(pendingUser);
            throw new IllegalArgumentException("Approval token has expired");
        }

        // Create User from PendingUser
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

        // Send confirmation email to user
        emailService.sendAccountApprovedEmail(user.getEmail(), user.getUsername());

        // Return a simple HTML response for the admin
        return ResponseEntity.ok(
                "<html><body><h2>User Approved Successfully</h2>" +
                        "<p>The user " + user.getUsername() + " has been approved.</p>" +
                        "<a href='" + frontendUrl + "'>Return to Application</a></body></html>"
        );
    }

    // Reject via dashboard
    @DeleteMapping("/admin/reject/{pendingUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectUser(@PathVariable Integer pendingUserId) {
        PendingUser pendingUser = pendingUserRepository.findById(pendingUserId)
                .orElseThrow(() -> new IllegalArgumentException("Pending user not found"));
        pendingUserRepository.delete(pendingUser);
        return ResponseEntity.ok().build();
    }

    // Get user by ID
=======
import com.example.naturelink.Entity.User;
import com.example.naturelink.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

>>>>>>> origin/ayoub
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

<<<<<<< HEAD
    // Add user
=======
>>>>>>> origin/ayoub
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

<<<<<<< HEAD
    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates
    ) {
        try {
            User updatedUser = userService.updateUser(id, updates);
=======
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
>>>>>>> origin/ayoub
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

<<<<<<< HEAD
    // Delete user
=======
>>>>>>> origin/ayoub
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
<<<<<<< HEAD

    // Block user
    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> blockUser(@PathVariable Integer id) {
        User blockedUser = userService.blockUser(id);
        return ResponseEntity.ok(blockedUser);
    }

    // Unblock user
    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unblockUser(@PathVariable Integer id) {
        User unblockedUser = userService.unblockUser(id);
        return ResponseEntity.ok(unblockedUser);
    }

    // Upload profile picture
    @PostMapping("/{id}/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Create uploads directory if not exists
            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String fileName = String.format("%d_%d_%s",
                    id,
                    System.currentTimeMillis(),
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
                            .replace(" ", "_")
                            .replace(",", "")
            );
            // Save file
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Update user profile picture
            User updatedUser = userService.updateProfilePic(id, fileName);

            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File storage error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
        }
    }
}
=======
}
>>>>>>> origin/ayoub
