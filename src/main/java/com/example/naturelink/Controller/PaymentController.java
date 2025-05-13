package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Payment;
import com.example.naturelink.Service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
 
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create-payment-intent/{reservationId}")
    public ResponseEntity<?> createPaymentIntent(@PathVariable Long reservationId,
                                                 @RequestParam Long userId) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(reservationId, userId);
            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "paymentIntentId", paymentIntent.getId()
            ));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        // Implement webhook to handle payment success/failure
        // You'll need to verify the webhook signature
        // and process events like payment_intent.succeeded
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment, Authentication authentication) {
        return ResponseEntity.ok(
                paymentService.createPayment(payment, authentication.getName())
        );
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getUserPayments(Authentication authentication) {
        return ResponseEntity.ok(
                paymentService.getUserPayments(authentication.getName())
        );
    }
    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> getPendingPayments(Authentication authentication) {
        return ResponseEntity.ok(
                paymentService.getPendingPayments(authentication.getName())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable Long id,
            @RequestBody Payment payment,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                paymentService.updatePayment(id, payment, authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        paymentService.deletePayment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

}