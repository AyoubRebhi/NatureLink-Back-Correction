package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Reservation;
import com.example.naturelink.Entity.TypeReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find all reservations by TypeReservation
    List<Reservation> findByTyperes(TypeReservation typeres);

    // Find reservations by TypeReservation and Logement (with non-null logement)
    List<Reservation> findByTyperesAndLogementIdIsNotNull(TypeReservation typeres);

    // Find reservations by TypeReservation and Event (with non-null event)
    List<Reservation> findByTyperesAndEventIdIsNotNull(TypeReservation typeres);

    // Find reservations by TypeReservation and Restaurant (with non-null restaurant)
    List<Reservation> findByTyperesAndRestaurantIdIsNotNull(TypeReservation typeres);

    // Find reservations by TypeReservation and Transport (with non-null transport)
    List<Reservation> findByTyperesAndTranspIdIsNotNull(TypeReservation typeres);

    // Check if a reservation exists by TypeReservation and ID
    boolean existsByTyperesAndId(TypeReservation typeres, Long id);

    // Find a reservation by TypeReservation and ID, returns Optional to handle missing reservation
    Optional<Reservation> findByTyperesAndId(TypeReservation typeres, Long id);
    List<Reservation> findByClient_Id(Long userId); // Fetch reservations by the client (user) ID
    // In ReservationRepository.java
    @Query("SELECT r FROM Reservation r WHERE r.client.id = :userId AND r.dateDebut >= :currentDate")
    List<Reservation> findUpcomingByClientId(@Param("userId") Long userId, @Param("currentDate") Date currentDate);

}
