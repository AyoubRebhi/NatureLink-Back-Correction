package com.example.naturelink.Controller;

import com.example.naturelink.Entity.Logement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")  // Enable CORS for this controller

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/logement-added")
    public void logementAdded(@RequestBody Logement logement) {
        messagingTemplate.convertAndSend("/topic/logements", logement);
    }
}
