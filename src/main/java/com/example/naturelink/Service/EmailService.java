package com.example.naturelink.Service;

import com.example.naturelink.Entity.PendingUser;
import com.example.naturelink.Repository.PendingUserRepository;
import com.example.naturelink.dto.ReservationDTO;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PendingUserRepository pendingUserRepository;
    
    @Autowired
    private REmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

// ✅ Use injected value instead of hardcoded URL
    @Value("${app.base.url}")
    private String backendUrl;    // ✅ Use injected value instead of hardcoded URL
    @Value("${frontend.url}")
    private String frontendUrl;
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

    @Async
    public void sendReservationEmail(ReservationDTO reservationDTO, String action) {
        logger.info("Sending email in thread: {}", Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        try {
            if (reservationDTO.getClientEmail() == null || reservationDTO.getClientEmail().isEmpty()) {
                logger.warn("No email address found for reservation {}. Skipping email notification for action: {}",
                        reservationDTO.getId(), action);
                return;
            }

            Context context = new Context();
            context.setVariable("clientName", reservationDTO.getClientNames().get(0));
            context.setVariable("reservationId", reservationDTO.getId());
            context.setVariable("type", reservationDTO.getTyperes().toString());
            context.setVariable("startDate", reservationDTO.getDateDebut().toString());
            context.setVariable("endDate", reservationDTO.getDateFin().toString());
            context.setVariable("status", reservationDTO.getStatut());
            context.setVariable("numClients", reservationDTO.getNumClients());

            String templateName;
            String subject;

            switch (action) {
                case "added":
                    templateName = "email/reservation-added";
                    subject = "NatureLink - Reservation Confirmation";
                    break;
                case "updated":
                    templateName = "email/reservation-updated";
                    subject = "NatureLink - Reservation Updated";
                    break;
                case "deleted":
                    templateName = "email/reservation-deleted";
                    subject = "NatureLink - Reservation Cancelled";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid email action: " + action);
            }

            emailService.sendReservationEmail(reservationDTO.getClientEmail(), subject, templateName, context);
            logger.info("Email sending took {} ms", System.currentTimeMillis() - startTime);
        } catch (MessagingException e) {
            logger.error("Failed to send email for reservation {} (action: {}): {}",
                    reservationDTO.getId(), action, e.getMessage());
        }
    }
}