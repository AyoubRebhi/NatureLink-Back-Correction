package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Payment;
import com.example.naturelink.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// PaymentRepository.java
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
    List<Payment> findByUserAndStatus(User user, Payment.PaymentStatus status);
    Optional<Payment> findByIdAndUser(Long id, User user);
    List<Payment> findByUserId(Integer userId);

    // Find payment by Stripe payment intent ID
    Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);

    // Find all payments by user ID
    List<Payment> findByUserId(Long userId);

    // Find all payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);

    // Find payment by reservation ID
    Optional<Payment> findByReservationId(Long reservationId);

    // Check if a reservation already has a completed payment
    boolean existsByReservationIdAndStatus(Long reservationId, Payment.PaymentStatus status);
}