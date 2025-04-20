    package com.example.naturelink.Service;
    
    import com.example.naturelink.dto.ReservationDTO;
    import com.example.naturelink.Entity.*;
    import com.example.naturelink.Repository.*;
    import com.lowagie.text.*;
    import com.lowagie.text.pdf.PdfWriter;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.scheduling.annotation.Async;
    import org.springframework.stereotype.Service;
    import org.thymeleaf.context.Context;
    import jakarta.mail.MessagingException;
    import java.io.ByteArrayInputStream;
    import java.io.ByteArrayOutputStream;
    import java.time.LocalDate;
    import java.util.Date;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;
    
    @Service
    public class ReservationService implements IReservationService {
    
        private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
        @Autowired
        private FlaskClientService flaskClientService;
        @Autowired
        private ReservationRepository reservationRepository;
    
        @Autowired
        private ILogementRepository logementRepository;
    
        @Autowired
        private IEventRepository evenementRepository;
    
        @Autowired
        private RestaurantRepository restaurantRepository;
    
        @Autowired
        private ITransportRepository transportRepository;
    
        @Autowired
        private IActivityRepository activityRepository;
    
        @Autowired
        private ExportPDFService exportPDFService;
    
        @Autowired
        private UserRepository userRepository;
    
    
        @Autowired
        private EmailService emailService; // Add this
    
    
        private Reservation convertToEntity(ReservationDTO dto) {
            Reservation reservation = new Reservation();
            if (dto.getUserId() != null) {
                Optional<User> userOpt = userRepository.findById(Math.toIntExact(dto.getUserId()));
                if (userOpt.isPresent()) {
                    reservation.setClient(userOpt.get());
                } else {
                    logger.warn("User not found for userId: {}. Setting client to null.", dto.getUserId());
                    reservation.setClient(null);
                }
            } else {
                logger.warn("UserId is null in DTO. Setting client to null.");
                reservation.setClient(null);
            }
            reservation.setDateDebut(dto.getDateDebut());
            reservation.setDateFin(dto.getDateFin());
            reservation.setStatut(dto.getStatut());
            reservation.setId(reservation.getId());
            reservation.setNumClients(dto.getNumClients());
            reservation.setClientNames(dto.getClientNames());
    
            if (dto.getLogementId() != null) {
                Logement logement = logementRepository.findById(dto.getLogementId())
                        .orElseThrow(() -> new RuntimeException("Logement not found"));
                reservation.setLogementId(logement);
                reservation.setTyperes(TypeReservation.LOGEMENT);
                reservation.setNumRooms(dto.getNumRooms());
            } else if (dto.getEventId() != null) {
                Event event = evenementRepository.findById(dto.getEventId())
                        .orElseThrow(() -> new RuntimeException("Event not found"));
                reservation.setEventId(event);
                reservation.setTyperes(TypeReservation.EVENT);
            } else if (dto.getRestaurantId() != null) {
                Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));
                reservation.setRestaurant(restaurant);
                reservation.setTyperes(TypeReservation.RESTAURANT);
            } else if (dto.getTransportId() != null) {
                Transport transport = transportRepository.findById(Long.valueOf(dto.getTransportId()))
                        .orElseThrow(() -> new RuntimeException("Transport not found"));
                reservation.setTranspId(transport);
                reservation.setTyperes(TypeReservation.TRANSPORT);
            } else if (dto.getActivityId() != null) {
                Activity activity = activityRepository.findById(Long.valueOf(dto.getActivityId()))
                        .orElseThrow(() -> new RuntimeException("Activity not found"));
                reservation.setActivityId(activity);
                reservation.setTyperes(TypeReservation.ACTIVITE);
            } else {
                throw new RuntimeException("A reservation type must be specified.");
            }
    
            return reservation;
        }
    
        private ReservationDTO convertToDTO(Reservation reservation) {
            Long userId = (reservation.getClient() != null && reservation.getClient().getId() != null)
                    ? Long.valueOf(reservation.getClient().getId())
                    : null;
            ReservationDTO dto = new ReservationDTO(
                    userId,
                    reservation.getDateDebut(),
                    reservation.getDateFin(),
                    reservation.getLogementId() != null ? Long.valueOf(reservation.getLogementId().getId()) : null,
                    reservation.getEventId() != null ? Long.valueOf(reservation.getEventId().getId()) : null,
                    reservation.getRestaurant() != null ? reservation.getRestaurant().getId() : null,
                    reservation.getTranspId() != null ? reservation.getTranspId().getId() : null,
                    reservation.getActivityId() != null ? reservation.getActivityId().getId() : null,
                    reservation.getStatut(),
                    reservation.getId(),
                    reservation.getNumClients(),
                    reservation.getNumRooms(),
                    reservation.getClientNames()
            );
            if (reservation.getClient() != null && reservation.getClient().getId() != null) {
                Optional<User> userOpt = userRepository.findById(reservation.getClient().getId());
                if (userOpt.isPresent()) {
                    dto.setClientEmail(userOpt.get().getEmail());
                } else {
                    logger.warn("User not found for userId: {}. Email will not be set for reservation {}",
                            reservation.getClient().getId(), reservation.getId());
                    dto.setClientEmail(null);
                }
            } else {
                logger.warn("Client is null for reservation {}. Email will not be set.", reservation.getId());
                dto.setClientEmail(null);
            }
            dto.setTyperes(String.valueOf(reservation.getTyperes()));
            return dto;
        }
    
        @Override
        public List<ReservationDTO> getAllReservations() {
            List<Reservation> reservations = reservationRepository.findAll();
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
        @Override
        public Optional<ReservationDTO> getReservationById(Long id) {
            Optional<Reservation> reservation = reservationRepository.findById(id);
            return reservation.map(this::convertToDTO);
        }
        @Override
        public ReservationDTO addReservation(ReservationDTO reservationDTO) {
            long startTime = System.currentTimeMillis();

            Reservation reservation = convertToEntity(reservationDTO);
            logger.info("convertToEntity took {} ms", System.currentTimeMillis() - startTime);
            logger.info("Client names to validate: {}", reservation.getClientNames());

            // Validate names
            startTime = System.currentTimeMillis();
            boolean areNamesValid = flaskClientService.areNamesValid(reservation.getClientNames());
            logger.info("Flask validation result: {}, took {} ms", areNamesValid, System.currentTimeMillis() - startTime);

            if (!areNamesValid) {
                logger.warn("Invalid client name(s) detected: {}", reservation.getClientNames());
                throw new RuntimeException("🚫 Invalid client name(s) detected by AI model.");
            }

            startTime = System.currentTimeMillis();
            Reservation savedReservation = reservationRepository.save(reservation);
            logger.info("reservationRepository.save took {} ms", System.currentTimeMillis() - startTime);

            startTime = System.currentTimeMillis();
            ReservationDTO savedDTO = convertToDTO(savedReservation);
            logger.info("convertToDTO took {} ms", System.currentTimeMillis() - startTime);

            startTime = System.currentTimeMillis();
            emailService.sendReservationEmail(savedDTO, "added");
            logger.info("emailService.sendReservationEmail call took {} ms", System.currentTimeMillis() - startTime);

            return savedDTO;
        }

        @Override
        public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
            if (reservationRepository.existsById(id)) {
                long startTime = System.currentTimeMillis();
                Reservation reservation = convertToEntity(reservationDTO);
                logger.info("convertToEntity took {} ms", System.currentTimeMillis() - startTime);
                logger.info("Client names to validate: {}", reservation.getClientNames());

                // Validate names
                startTime = System.currentTimeMillis();
                boolean areNamesValid = flaskClientService.areNamesValid(reservation.getClientNames());
                logger.info("Flask validation result: {}, took {} ms", areNamesValid, System.currentTimeMillis() - startTime);

                if (!areNamesValid) {
                    logger.warn("Invalid client name(s) detected: {}", reservation.getClientNames());
                    throw new RuntimeException("🚫 Invalid client name(s) detected by AI model.");
                }

                reservation.setId(id);
                startTime = System.currentTimeMillis();
                Reservation updatedReservation = reservationRepository.save(reservation);
                logger.info("reservationRepository.save took {} ms", System.currentTimeMillis() - startTime);

                startTime = System.currentTimeMillis();
                ReservationDTO updatedDTO = convertToDTO(updatedReservation);
                logger.info("convertToDTO took {} ms", System.currentTimeMillis() - startTime);

                startTime = System.currentTimeMillis();
                emailService.sendReservationEmail(updatedDTO, "updated");
                logger.info("emailService.sendReservationEmail call took {} ms", System.currentTimeMillis() - startTime);

                return updatedDTO;
            }
            throw new RuntimeException("Reservation not found");
        }


        @Override
        public void deleteReservation(Long id) {
            long startTime = System.currentTimeMillis();
            Optional<Reservation> reservationOpt = reservationRepository.findById(id);
            logger.info("reservationRepository.findById took {} ms", System.currentTimeMillis() - startTime);
    
            if (reservationOpt.isPresent()) {
                startTime = System.currentTimeMillis();
                ReservationDTO reservationDTO = convertToDTO(reservationOpt.get());
                logger.info("convertToDTO took {} ms", System.currentTimeMillis() - startTime);
    
                startTime = System.currentTimeMillis();
                reservationRepository.deleteById(id);
                logger.info("reservationRepository.deleteById took {} ms", System.currentTimeMillis() - startTime);
    
                startTime = System.currentTimeMillis();
                emailService.sendReservationEmail(reservationDTO, "deleted");
                logger.info("emailService.sendReservationEmail call took {} ms", System.currentTimeMillis() - startTime);
            } else {
                throw new RuntimeException("Reservation not found");
            }
        }
    
        @Override
        public List<ReservationDTO> getReservationsByType(TypeReservation typeres) {
            List<Reservation> reservations = reservationRepository.findByTyperes(typeres);
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
        @Override
        public List<ReservationDTO> getReservationsByTypeAndLogement(TypeReservation typeres) {
            List<Reservation> reservations = reservationRepository.findByTyperesAndLogementIdIsNotNull(typeres);
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
        @Override
        public List<ReservationDTO> getReservationsByTypeAndEvent(TypeReservation typeres) {
            List<Reservation> reservations = reservationRepository.findByTyperesAndEventIdIsNotNull(typeres);
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
        @Override
        public List<ReservationDTO> getReservationsByTypeAndRestaurant(TypeReservation typeres) {
            List<Reservation> reservations = reservationRepository.findByTyperesAndRestaurantIdIsNotNull(typeres);
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
        @Override
        public List<ReservationDTO> getReservationsByTypeAndTransport(TypeReservation typeres) {
            List<Reservation> reservations = reservationRepository.findByTyperesAndTranspIdIsNotNull(typeres);
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
        @Override
        public Optional<ReservationDTO> getReservationByTypeAndId(TypeReservation typeres, Long id) {
            Optional<Reservation> reservation = reservationRepository.findByTyperesAndId(typeres, id);
            return reservation.map(this::convertToDTO);
        }

        @Override
        public ReservationDTO addReservationByType(TypeReservation typeres, ReservationDTO reservationDTO) {
            logger.info("Adding reservation by type: {}, payload: {}", typeres, reservationDTO);
            if (reservationDTO == null) {
                logger.error("ReservationDTO is null for type: {}", typeres);
                throw new RuntimeException("Invalid reservation data: DTO is null");
            }
            if (reservationDTO.getClientNames() == null || reservationDTO.getClientNames().isEmpty()) {
                logger.error("Client names are null or empty for type: {}, DTO: {}", typeres, reservationDTO);
                throw new RuntimeException("Invalid reservation data: client names are required");
            }
            reservationDTO.setTyperes(String.valueOf(typeres));
            return addReservation(reservationDTO); // Calls addReservation with validation
        }
    
        @Override
        public ReservationDTO updateReservationByType(TypeReservation typeres, Long id, ReservationDTO reservationDTO) {
            if (reservationRepository.existsByTyperesAndId(typeres, id)) {
                Reservation reservation = convertToEntity(reservationDTO);
                reservation.setId(id);
                reservation.setTyperes(typeres);
                Reservation updatedReservation = reservationRepository.save(reservation);
                ReservationDTO updatedDTO = convertToDTO(updatedReservation);
                emailService.sendReservationEmail(updatedDTO, "updated");
                return updatedDTO;
            }
            throw new RuntimeException("Reservation not found");
        }
    
        @Override
        public void deleteReservationByType(TypeReservation typeres, Long id) {
            if (reservationRepository.existsByTyperesAndId(typeres, id)) {
                Optional<Reservation> reservationOpt = reservationRepository.findById(id);
                if (reservationOpt.isPresent()) {
                    ReservationDTO reservationDTO = convertToDTO(reservationOpt.get());
                    reservationRepository.deleteById(id);
                    emailService.sendReservationEmail(reservationDTO, "deleted");
                } else {
                    throw new RuntimeException("Reservation not found");
                }
            } else {
                throw new RuntimeException("Reservation not found");
            }
        }
    
        public List<ReservationDTO> getReservationsByUserIdDTO(Long userId) {
            List<Reservation> reservations = reservationRepository.findByClient_Id(Long.valueOf(Math.toIntExact(userId)));
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        public ByteArrayInputStream generateReservationPDF(Long reservationId, Reservation reservation) {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
    
            try {
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();
    
                Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("Reservation Details", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
    
                Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
                Paragraph reservationIdParagraph = new Paragraph("Reservation ID: " + reservationId, headerFont);
                reservationIdParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(reservationIdParagraph);
    
                document.add(Chunk.NEWLINE);
    
                Font regularFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
                Paragraph clientNames = new Paragraph("Client Names: " + String.join(", ", reservation.getClientNames()), regularFont);
                document.add(clientNames);
    
                Paragraph dateDebut = new Paragraph("Start Date: " + reservation.getDateDebut(), regularFont);
                document.add(dateDebut);
    
                Paragraph dateFin = new Paragraph("End Date: " + reservation.getDateFin(), regularFont);
                document.add(dateFin);
    
                Paragraph statut = new Paragraph("Status: " + reservation.getStatut(), regularFont);
                document.add(statut);
    
                Paragraph reservationType = new Paragraph("Reservation Type: " + reservation.getTyperes(), regularFont);
                document.add(reservationType);
    
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
    
                Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
                Paragraph footer = new Paragraph("Thank you for booking with us. For any inquiries, contact support@example.com", footerFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);
            } catch (DocumentException e) {
                e.printStackTrace();
            } finally {
                document.close();
            }
    
            return new ByteArrayInputStream(out.toByteArray());
        }
    
        public List<ReservationDTO> getUpcomingReservationsByUserId(Long userId) {
            Date currentDate = new Date();
            List<Reservation> reservations = reservationRepository.findUpcomingByClientId(Long.valueOf(Math.toIntExact(userId)), currentDate);
            return reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    
    }