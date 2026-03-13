package com.example.lifetracker.controllers;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exeptions.NotFoundExeption;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.example.lifetracker.services.EventService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return this.eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable int id, HttpServletResponse response) {

        try {
            return this.eventService.getEventById(id);
        }
        catch (NotFoundExeption ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

    }

    @PatchMapping("/{id}")
    public Event updateEvent(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        return eventService.updateEvent(id, updates);
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return this.eventService.createEvent(event);
    }

}