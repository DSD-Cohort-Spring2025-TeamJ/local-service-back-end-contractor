package com.localservice.localservice_api.controller;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.localservice.localservice_api.dto.EventRequest;
import com.localservice.localservice_api.service.GoogleCalendarService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(@RequestParam String userId) throws IOException, GeneralSecurityException {
        return googleCalendarService.getAllEvents(userId);
    }

    @PostMapping("/events")
    public Event createEvent(@RequestParam String userId, @RequestBody EventRequest eventRequest) throws IOException, GeneralSecurityException {
        Event event = new Event()
                .setSummary(eventRequest.getSummary());

        // Convert String dateTime to EventDateTime
        event.setStart(new EventDateTime().setDateTime(eventRequest.getParsedStartTime()));
        event.setEnd(new EventDateTime().setDateTime(eventRequest.getParsedEndTime()));

        return googleCalendarService.createEvent(userId, event);

    }
}
