package com.example.naturelink.Service;

import com.example.naturelink.Entity.Payment;
import com.example.naturelink.Entity.Reservation;
import com.example.naturelink.Entity.User;
import com.example.naturelink.Repository.PaymentRepository;
import com.example.naturelink.Repository.ReservationRepository;
import com.example.naturelink.Repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public PaymentIntent createPaymentIntent(Long reservationId, Long userId) throws StripeException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate amount based on reservation type and details
        long amount = calculateAmount(reservation);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("eur")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save payment record
        Payment payment = Payment.builder()
                .amount((double) amount / 100) // convert from cents to euros
                .currency("EUR")
                .status(Payment.PaymentStatus.PENDING)
                .stripePaymentIntentId(paymentIntent.getId())
                .stripeClientSecret(paymentIntent.getClientSecret())
                .user(user)
                .reservation(reservation)
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return paymentIntent;
    }

    private long calculateAmount(Reservation reservation) {
        // Implement your business logic to calculate amount based on:
        // - Reservation type (logement, event, restaurant, etc.)
        // - Number of clients/rooms
        // - Duration
        // Return amount in cents (e.g., 1000 for 10.00 EUR)
        return 1000L; // Example value
    }

    public void handlePaymentSuccess(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Update reservation status
        Reservation reservation = payment.getReservation();
        reservation.setStatut("Confirmée");
        reservationRepository.save(reservation);
    }

    public void handlePaymentFailure(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    public Payment createPayment(Payment payment, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        payment.setUser(user);
        payment.setPaymentDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public List<Payment> getUserPayments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentRepository.findByUser(user);
    }
    public Payment updatePayment(Long paymentId, Payment updatedPayment, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment existing = paymentRepository.findByIdAndUser(paymentId, user)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        existing.setAmount(updatedPayment.getAmount());
        existing.setPaymentMethod(updatedPayment.getPaymentMethod());
        existing.setStatus(updatedPayment.getStatus());

        return paymentRepository.save(existing);
    }

    public void deletePayment(Long paymentId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = paymentRepository.findByIdAndUser(paymentId, user)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        paymentRepository.delete(payment);
    }

    public List<Payment> getPendingPayments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentRepository.findByUserAndStatus(user, Payment.PaymentStatus.PENDING);
    }
    public List<Payment> getPaymentsByUserId(Integer userId) {
        // Exemple simple
        return paymentRepository.findByUserId(userId);
    }

}