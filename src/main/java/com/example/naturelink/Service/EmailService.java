package com.example.naturelink.Service;

import com.example.naturelink.Entity.PendingUser;
import com.example.naturelink.Repository.PendingUserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PendingUserRepository pendingUserRepository;

    private final String backendUrl = "http://localhost:9000"; // Hardcoded backend URL
    private final String frontendUrl = "http://localhost:4200"; // Hardcoded frontend URL

    public void sendApprovalRequestEmail(String adminEmail, String username, String role, String proofDocument, PendingUser pendingUser) {
        // Generate unique approval token
        String approvalToken = UUID.randomUUID().toString();
        pendingUser.setApprovalToken(approvalToken);
        pendingUser.setExpiryDate(java.time.LocalDateTime.now().plusHours(24)); // Token expires in 24 hours
        pendingUserRepository.save(pendingUser);

        // Construct approval link pointing to backend
        String approvalLink = backendUrl + "/api/users/approve?token=" + approvalToken;

        var mailMessage = new org.springframework.mail.SimpleMailMessage();
        mailMessage.setTo(adminEmail);
        mailMessage.setSubject("New Registration Approval Request");
        mailMessage.setText(
                "Dear Admin,\n\n" +
                        "A new registration request requires your approval:\n" +
                        "Username: " + username + "\n" +
                        "Role: " + role + "\n" +
                        "Proof Document: " + proofDocument + "\n\n" +
                        "you can approve or reject this request from the admin dashboard.\n\n" +
                        "Best regards,\nNatureLink Team"
        );
        mailSender.send(mailMessage);
    }

    public void sendAccountApprovedEmail(String userEmail, String username) {
        var mailMessage = new org.springframework.mail.SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("Your NatureLink Account Has Been Approved");
        mailMessage.setText(
                "Dear " + username + ",\n\n" +
                        "Congratulations! Your NatureLink account has been approved.\n\n" +
                        "You can now log in to your account using the following credentials:\n" +
                        "Username: " + username + "\n" +
                        "Login URL: " + frontendUrl + "/login\n\n" +
                        "If you have any questions, please contact our support team.\n\n" +
                        "Best regards,\nNatureLink Team"
        );
        mailSender.send(mailMessage);
    }
}