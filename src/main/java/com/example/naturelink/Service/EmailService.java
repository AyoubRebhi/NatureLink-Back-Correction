package com.example.naturelink.Service;

import com.example.naturelink.dto.ReservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;

@Service
public class EmailService {

    @Autowired
    private REmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

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