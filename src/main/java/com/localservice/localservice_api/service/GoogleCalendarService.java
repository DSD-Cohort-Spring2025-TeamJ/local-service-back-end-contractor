package com.localservice.localservice_api.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Logger;


@Service
public class GoogleCalendarService {
    private static final Logger logger = Logger.getLogger(GoogleCalendarService.class.getName());
    private final GoogleCredentialService googleCredentialService;

    public GoogleCalendarService(GoogleCredentialService googleCredentialService) {
        this.googleCredentialService = googleCredentialService;
    }

    public List<Event> getAllEvents(String userId) throws IOException, GeneralSecurityException {
        Credential credential = googleCredentialService.getStoredCredential(userId);
        if (credential == null) {
            throw new IOException("User not authenticated. Please login first.");
        }

        Calendar calendarService = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Google Calendar API Spring Boot")
                .build();

        Events events = calendarService.events().list("primary").execute();
        return events.getItems();
    }

    public Event createEvent(String userId, Event event) throws IOException, GeneralSecurityException {
        Credential credential = googleCredentialService.getStoredCredential(userId);
        if (credential == null) {
            throw new IOException("User not authenticated. Please login first.");
        }

        Calendar calendarService = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Google Calendar API Spring Boot")
                .build();

        return calendarService.events().insert("primary", event).execute();
    }

}
