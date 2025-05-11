package com.example.naturelink.Service;

import com.example.naturelink.Entity.*;
import com.example.naturelink.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.naturelink.dto.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PackService implements IPackService {

    @Autowired private PackRepository packRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ILogementRepository logementRepository;
    @Autowired private ITransportRepository transportRepository;
    @Autowired private IActivityRepository activityRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private IEventRepository evenementRepository;
    @Autowired private RatingService ratingService;  // Inject RatingService


    @Override
    @Transactional
    public List<PackDTO> getAllPacks() {
        List<Pack> packs = packRepository.findAll();

        return packs.stream().map(pack -> {
            PackDTO dto = new PackDTO();
            dto.setNom(pack.getNom());
            dto.setPrix(pack.getPrix());
            dto.setDescription(pack.getDescription());
            dto.setId(pack.getId());
            // Safely handle null User and null User ID
            dto.setUserId(pack.getUser() != null && pack.getUser().getId() != null
                    ? pack.getUser().getId().longValue()
                    : null);

            dto.setLogements(pack.getLogements().stream().map(Logement::getId).map(Long::valueOf).collect(Collectors.toList()));
            dto.setTransports(pack.getTransports().stream().map(Transport::getId).map(Long::valueOf).collect(Collectors.toList()));
            dto.setActivities(pack.getActivities().stream().map(Activity::getId).map(Long::valueOf).collect(Collectors.toList()));
            dto.setRestaurants(pack.getRestaurants().stream().map(Restaurant::getId).map(Long::valueOf).collect(Collectors.toList()));
            dto.setEvenements(pack.getEvenements().stream().map(Event::getId).map(Long::valueOf).collect(Collectors.toList()));

            // Calculate average rating for the pack
            double avgRating = ratingService.getAverageRatingForPack(pack.getId());
            dto.setAverageRating(avgRating);  // Set the average rating for the pack

            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public PackDTO getPackById(Long id) {
        // Validate input ID
        if (id == null) {
            throw new IllegalArgumentException("Pack ID cannot be null");
        }

        // Fetch pack from repository
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pack not found with id: " + id));

        // Map to DTO
        PackDTO dto = new PackDTO();
        dto.setNom(pack.getNom());
        dto.setPrix(pack.getPrix());
        dto.setDescription(pack.getDescription());
        dto.setId(pack.getId());
        // Safely handle null User and null User ID (matching getAllPacks)
        dto.setUserId(pack.getUser() != null && pack.getUser().getId() != null
                ? pack.getUser().getId().longValue()
                : null);

        dto.setLogements(pack.getLogements().stream().map(Logement::getId).map(Long::valueOf).collect(Collectors.toList()));
        dto.setTransports(pack.getTransports().stream().map(Transport::getId).map(Long::valueOf).collect(Collectors.toList()));
        dto.setActivities(pack.getActivities().stream().map(Activity::getId).map(Long::valueOf).collect(Collectors.toList()));
        dto.setRestaurants(pack.getRestaurants().stream().map(Restaurant::getId).map(Long::valueOf).collect(Collectors.toList()));
        dto.setEvenements(pack.getEvenements().stream().map(Event::getId).map(Long::valueOf).collect(Collectors.toList()));

        // Calculate average rating for the pack
        double avgRating = ratingService.getAverageRatingForPack(pack.getId());
        dto.setAverageRating(avgRating);  // Set the average rating for the pack

        return dto;
    }
        public Pack addPack(PackDTO dto) {
            if (dto == null) {
                throw new IllegalArgumentException("PackDTO cannot be null");
            }
    
            Pack pack = new Pack();
            pack.setNom(dto.getNom());
            pack.setDescription(dto.getDescription());
            pack.setPrix(dto.getPrix());
    
            int associatedCount = 0;
    
            // Logements
            if (dto.getLogements() != null && !dto.getLogements().isEmpty()) {
                List<Logement> logements = logementRepository.findAllById(dto.getLogements());
                pack.setLogements(logements);
                associatedCount++;
            }
    
            // Restaurants
            if (dto.getRestaurants() != null && !dto.getRestaurants().isEmpty()) {
                List<Restaurant> restaurants = restaurantRepository.findAllById(dto.getRestaurants());
                pack.setRestaurants(restaurants);
                associatedCount++;
            }
    
            // Activities
            if (dto.getActivities() != null && !dto.getActivities().isEmpty()) {
                List<Activity> activities = activityRepository.findAllById(dto.getActivities());
                pack.setActivities(activities);
                associatedCount++;
            }
    
            // Transports
            if (dto.getTransports() != null && !dto.getTransports().isEmpty()) {
                List<Transport> transports = transportRepository.findAllById(dto.getTransports());
                pack.setTransports(transports);
                associatedCount++;
            }
    
            // Evenements
            if (dto.getEvenements() != null && !dto.getEvenements().isEmpty()) {
                List<Event> evenements = evenementRepository.findAllById(dto.getEvenements());
                pack.setEvenements(evenements);
                associatedCount++;
            }
    
            // Check: Must have at least 2 types of entities associated
            if (associatedCount < 2) {
                throw new IllegalArgumentException("A pack must contain at least two associated elements.");
            }
    
            // Static user assignment
            User user = userRepository.findById(Math.toIntExact(dto.getUserId()))
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
            pack.setUser(user);
    
            return packRepository.save(pack);
        }


    @Override
    public Pack updatePack(Long id, PackDTO dto) {
        Pack existingPack = packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pack not found"));

        existingPack.setNom(dto.getNom());
        existingPack.setPrix(dto.getPrix());
        existingPack.setDescription(dto.getDescription());

        // Directly use List<Long>
        existingPack.setLogements(logementRepository.findAllById(dto.getLogements()));
        existingPack.setTransports(transportRepository.findAllById(dto.getTransports()));
        existingPack.setActivities(activityRepository.findAllById(dto.getActivities()));
        existingPack.setRestaurants(restaurantRepository.findAllById(dto.getRestaurants()));
        existingPack.setEvenements(evenementRepository.findAllById(dto.getEvenements()));

        return packRepository.save(existingPack);
    }


    @Override
    public void deletePack(Long id) {
        packRepository.deleteById(id);
    }
}
