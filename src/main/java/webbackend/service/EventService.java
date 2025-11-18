package webbackend.service;

import webbackend.entity.Event;
import webbackend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> findAll() { return eventRepository.findAll(); }

    public Event findEventByType(String type){ return eventRepository.findEventByType(type); }

    public List<Event> findEventsByType(String type){ return eventRepository.findEventsByType(type); }

    public Event findById(UUID id){ return eventRepository.findById(id).orElse(null); }

    public Event save(Event e){ return eventRepository.save(e); }




}

