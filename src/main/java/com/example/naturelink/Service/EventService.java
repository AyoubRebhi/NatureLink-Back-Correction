package com.example.naturelink.Service;

import com.example.naturelink.Entity.Event;
import com.example.naturelink.Repository.IEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final IEventRepository eventRepository;

public Event createEvent(Event event) {
    return eventRepository.save(event);
}
public List<Event> getAllEvents() {
    return eventRepository.findAll();
}

public Event getEventById(int id) {
    return eventRepository.findById((long) id).orElse(null);
}


public Event updateEvent(Event event) {
    return eventRepository.save(event);
}
public void deleteEventById(int id) {
    eventRepository.deleteById((long) id);
}
}
