package com.localservice.localservice_api.controller;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.localservice.localservice_api.dto.EventRequest;
import com.localservice.localservice_api.service.GoogleCalendarService;
import com.localservice.localservice_api.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final JwtService jwtService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService, JwtService jwtService) {
        this.googleCalendarService = googleCalendarService;
        this.jwtService = jwtService;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(@RequestHeader("Authorization") String authHeader) throws IOException, GeneralSecurityException {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtService.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String userId = jwtService.getUserIdFromToken(token);
        return googleCalendarService.getAllEvents(userId);
    }

    @PostMapping("/events")
    public Event createEvent(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EventRequest eventRequest
    ) throws IOException, GeneralSecurityException {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtService.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String userId = jwtService.getUserIdFromToken(token);

        Event event = new Event()
                .setSummary(eventRequest.getSummary())
                .setStart(new EventDateTime().setDateTime(eventRequest.getParsedStartTime()))
                .setEnd(new EventDateTime().setDateTime(eventRequest.getParsedEndTime()));

        return googleCalendarService.createEvent(userId, event);
    }
}
