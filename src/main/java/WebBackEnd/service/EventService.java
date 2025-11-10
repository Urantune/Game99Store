package WebBackEnd.service;

import WebBackEnd.Entity.Event;
import WebBackEnd.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;


    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findEventByType(String type){
        return eventRepository.findEventByType(type);
    }

    public List<Event> findEventsByType(String type){
        return eventRepository.findEventsByType(type);
    }
}
