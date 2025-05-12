package com.example.naturelink.Controller;

import com.example.naturelink.Service.IReservationService;
import com.example.naturelink.dto.ReservationDTO;
import com.example.naturelink.Entity.TypeReservation;
import com.example.naturelink.Service.ExportPDFService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private ExportPDFService exportPDFService;

    // Get all reservations
    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // Get a reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        Optional<ReservationDTO> reservationDTO = reservationService.getReservationById(id);
        return reservationDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationDTO reservationDTO) {
        try {
            logger.info("Received reservation payload: {}", reservationDTO);

            // Validate required fields
            if (reservationDTO.getUserId() == null ||
                    reservationDTO.getDateDebut() == null ||
                    reservationDTO.getDateFin() == null ||
                    reservationDTO.getNumClients() == null ||
                    reservationDTO.getClientNames() == null || reservationDTO.getClientNames().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "User ID, start date, end date, numClients, and clientNames are required."));
            }

            // Route to appropriate reservation type
            ReservationDTO savedReservation;
            if (reservationDTO.getPackId() != null) {
                // Handle pack reservation
                savedReservation = reservationService.addReservationByType(TypeReservation.PACK, reservationDTO);
            } else if (reservationDTO.getLogementId() != null) {
                savedReservation = reservationService.addReservationByType(TypeReservation.LOGEMENT, reservationDTO);
            } else if (reservationDTO.getEventId() != null) {
                savedReservation = reservationService.addReservationByType(TypeReservation.EVENT, reservationDTO);
            } else if (reservationDTO.getRestaurantId() != null) {
                savedReservation = reservationService.addReservationByType(TypeReservation.RESTAURANT, reservationDTO);
            } else if (reservationDTO.getTransportId() != null) {
                savedReservation = reservationService.addReservationByType(TypeReservation.TRANSPORT, reservationDTO);
            } else if (reservationDTO.getActivityId() != null) {
                savedReservation = reservationService.addReservationByType(TypeReservation.ACTIVITE, reservationDTO);
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "At least one entity (logement, event, restaurant, transport, activity, or pack) must be provided."));
            }

            return ResponseEntity.ok(savedReservation);
        } catch (RuntimeException e) {
            logger.error("Error creating reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    // Update an existing reservation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO) {
        try {
            logger.info("Updating reservation ID {} with payload: {}", id, reservationDTO);
            ReservationDTO updatedReservation = reservationService.updateReservation(id, reservationDTO);
            return ResponseEntity.ok(updatedReservation);
        } catch (RuntimeException e) {
            logger.error("Error updating reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    // Delete a reservation by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting reservation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Get reservations by type
    @GetMapping("/type/{typeres}")
    public List<ReservationDTO> getReservationsByType(@PathVariable TypeReservation typeres) {
        return reservationService.getReservationsByType(typeres);
    }

    // Get reservations by type and logement
    @GetMapping("/type/{typeres}/logement")
    public List<ReservationDTO> getReservationsByTypeAndLogement(@PathVariable TypeReservation typeres) {
        return reservationService.getReservationsByTypeAndLogement(typeres);
    }

    // Get reservations by type and event
    @GetMapping("/type/{typeres}/event")
    public List<ReservationDTO> getReservationsByTypeAndEvent(@PathVariable TypeReservation typeres) {
        return reservationService.getReservationsByTypeAndEvent(typeres);
    }

    // Get reservations by type and restaurant
    @GetMapping("/type/{typeres}/restaurant")
    public List<ReservationDTO> getReservationsByTypeAndRestaurant(@PathVariable TypeReservation typeres) {
        return reservationService.getReservationsByTypeAndRestaurant(typeres);
    }

    // Get reservations by type and transport
    @GetMapping("/type/{typeres}/transport")
    public List<ReservationDTO> getReservationsByTypeAndTransport(@PathVariable TypeReservation typeres) {
        return reservationService.getReservationsByTypeAndTransport(typeres);
    }

    // Get reservation by type and ID
    @GetMapping("/type/{typeres}/{id}")
    public ResponseEntity<ReservationDTO> getReservationByTypeAndId(@PathVariable TypeReservation typeres,
                                                                    @PathVariable Long id) {
        Optional<ReservationDTO> reservationDTO = reservationService.getReservationByTypeAndId(typeres, id);
        return reservationDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a reservation by type
    @PostMapping("/type/{typeres}")
    public ResponseEntity<?> addReservationByType(@PathVariable TypeReservation typeres,
                                                  @RequestBody ReservationDTO reservationDTO) {
        try {
            logger.info("Adding reservation by type {} with payload: {}", typeres, reservationDTO);
            ReservationDTO savedReservation = reservationService.addReservationByType(typeres, reservationDTO);
            return ResponseEntity.ok(savedReservation);
        } catch (RuntimeException e) {
            logger.error("Error adding reservation by type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    // Update a reservation by type
    @PutMapping("/type/{typeres}/{id}")
    public ResponseEntity<?> updateReservationByType(@PathVariable TypeReservation typeres,
                                                     @PathVariable Long id,
                                                     @RequestBody ReservationDTO reservationDTO) {
        try {
            logger.info("Updating reservation by type {} ID {} with payload: {}", typeres, id, reservationDTO);
            ReservationDTO updatedReservation = reservationService.updateReservationByType(typeres, id, reservationDTO);
            return ResponseEntity.ok(updatedReservation);
        } catch (RuntimeException e) {
            logger.error("Error updating reservation by type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    // Delete a reservation by type
    @DeleteMapping("/type/{typeres}/{id}")
    public ResponseEntity<Void> deleteReservationByType(@PathVariable TypeReservation typeres,
                                                        @PathVariable Long id) {
        try {
            reservationService.deleteReservationByType(typeres, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting reservation by type: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUserId(@PathVariable Long userId) {
        List<ReservationDTO> reservations = reservationService.getReservationsByUserIdDTO(userId);
        if (reservations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reservations);
    }

    // Endpoint to generate and download reservation PDF
    @GetMapping("/api/reservations/{id}/pdf")
    public ResponseEntity<byte[]> downloadReservationPDF(@PathVariable("id") Long id) {
        Optional<ReservationDTO> reservationOpt = reservationService.getReservationById(id);
        if (!reservationOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        ReservationDTO reservation = reservationOpt.get();
        ByteArrayInputStream bis = exportPDFService.generateReservationPDF(id, reservation);
        byte[] pdfContent = bis.readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=reservation_" + id + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<ReservationDTO>> getUpcomingReservationsByUserId(@PathVariable Long userId) {
        List<ReservationDTO> reservations = reservationService.getUpcomingReservationsByUserId(userId);
        if (reservations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reservations);
    }
}