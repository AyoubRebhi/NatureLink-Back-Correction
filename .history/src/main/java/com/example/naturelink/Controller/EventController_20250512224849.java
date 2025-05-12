package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Event;
import com.example.naturelink.Service.EventService;
import com.example.naturelink.Service.ExportPDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
public class EventController {
    private final EventService eventService;
    private final ExportPDFService exportPDFService;
    private final RestTemplate restTemplate;


    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportAllEventsToPdf() {
        var events = eventService.getAllEvents();
        var pdf = exportPDFService.exportEventsToPdf(events);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=events.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }


    @PostMapping("/add")
    public Event createEvent(@RequestBody Event event) {return eventService.createEvent(event);
    }


    @GetMapping("/All")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/All/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable int id) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }


    @PutMapping("/All/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable int id,@RequestBody Event event) {

        Event updatedEvent = eventService.getEventById(id);
    if (updatedEvent == null) {
    return ResponseEntity.notFound().build();
    }
    updatedEvent.setDate(event.getDate());
    updatedEvent.setDescription(event.getDescription());
    updatedEvent.setLocation(event.getLocation());
    updatedEvent.setFounder(event.getFounder());
    updatedEvent.setNbrplace(event.getNbrplace());
    updatedEvent.setTitle(event.getTitle());
    updatedEvent.setImage(event.getImage());
        Event event1 = eventService.updateEvent(updatedEvent);
return ResponseEntity.ok(event1);
    }



    @DeleteMapping("/All/delete/{id}")
    public ResponseEntity<Event> deleteEvent(@PathVariable int id) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        eventService.deleteEventById(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value="/recommend",  consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recommendEvents(@RequestBody Map<String, Object> payload) {
        // Flask endpoint URL for event recommendations
        String flaskUrl = "http://localhost:5000/recommend/event";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Ensure application/json is set

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);
            return ResponseEntity.ok().body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
        // Send the data (user_input, events, top_n) to the Flask service for recommendations
       // Map<String, Object> response = restTemplate.postForObject(flaskUrl, payload, Map.class);

        // Return the response from Flask (recommended events)
        //return ResponseEntity.ok(response);
    }
}
