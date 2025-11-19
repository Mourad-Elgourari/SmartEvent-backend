package com.congress.event.service;

import com.congress.event.model.Event;
import com.congress.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(updatedEvent.getTitle());
                    event.setDescription(updatedEvent.getDescription());
                    event.setImage(updatedEvent.getImage());
                    event.setStartDate(updatedEvent.getStartDate());
                    event.setEndDate(updatedEvent.getEndDate());
                    event.setVisibility(updatedEvent.getVisibility());
                    event.setType(updatedEvent.getType());
                    event.setSponsors(updatedEvent.getSponsors());
                    event.setSocialNetwork(updatedEvent.getSocialNetwork());
                    event.setStatus(updatedEvent.getStatus());
                    event.setOrganizers(updatedEvent.getOrganizers());
                    event.setBadge(updatedEvent.getBadge());
                    event.setCreatedBy(updatedEvent.getCreatedBy());
                    event.setCategory(updatedEvent.getCategory());
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
