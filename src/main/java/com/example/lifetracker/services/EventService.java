package com.example.lifetracker.services;

import com.example.lifetracker.domains.Event;
import com.example.lifetracker.exeptions.NotFoundExeption;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private static List<Event> events = new ArrayList<>();

    public EventService() {

        if (events.isEmpty()) {

            events.add(new Event(
                    1,
                    "Trip",
                    "Weekend trip with friends",
                    LocalDate.of(2025, 6, 12),
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0),
                    "Amsterdam",
                    500
            ));

            events.add(new Event(
                    2,
                    "Birthday Party",
                    "Anna's 22nd birthday",
                    LocalDate.of(2025, 7, 3),
                    LocalTime.of(19, 0),
                    null,
                    "Rotterdam",
                    300
            ));
        }
    }

    public List<Event> getAllEvents() {
        return events;
    }

    public Event getEventById(int id) {
        return events.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundExeption("Event not found"));
    }

    public Event updateEvent(int id, Map<String, Object> updates) {
        Event event = getEventById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> event.setTitle((String) value);
                case "description" -> event.setDescription((String) value);
                case "location" -> event.setLocation((String) value);
                case "expenses" -> event.setExpenses(Double.parseDouble(value.toString()));
                case "date" -> event.setDate(LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                case "startTime" -> event.setStartTime(LocalTime.parse(value.toString(), DateTimeFormatter.ofPattern("HH:mm")));
                case "endTime" -> event.setEndTime(LocalTime.parse(value.toString(), DateTimeFormatter.ofPattern("HH:mm"))
                );
            }
        });

        return event;
    }

    public Event createEvent(Event event) {

        int newId = events.size() + 1;

        Event newEvent = new Event(
                newId,
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getExpenses()
        );

        events.add(newEvent);

        return newEvent;
    }
}
