package com.localservice.localservice_api.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Logger;


@Service
public class GoogleCalendarService {
    private static final Logger logger = Logger.getLogger(GoogleCalendarService.class.getName());
    private final GoogleCredentialService googleCredentialService;
    private final AppointmentRepository appointmentRepository;

    public GoogleCalendarService(GoogleCredentialService googleCredentialService, AppointmentRepository appointmentRepository) {
        this.googleCredentialService = googleCredentialService;
        this.appointmentRepository = appointmentRepository;
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

    public void syncUnsyncedAppointmentsToCalendar(String userId) throws IOException, GeneralSecurityException {
        List<Appointment> unsyncedAppointments = appointmentRepository.findAllUnsyncedAppointments();

        var credential = googleCredentialService.getStoredCredential(userId);

        Calendar calendarService = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Booking App").build();

        for (Appointment appointment : unsyncedAppointments) {
            Event event = new Event()
                    .setSummary("Appointment with " + appointment.getClient_name())
                    .setDescription(appointment.getIssue_description())
                    .setStart(new EventDateTime().setDateTime(
                            new DateTime(appointment.getStart_time().atZone(java.time.ZoneId.systemDefault()).toInstant().toString())))
                    .setEnd(new EventDateTime().setDateTime(
                            new DateTime(appointment.getEnd_time().atZone(java.time.ZoneId.systemDefault()).toInstant().toString())))
                    .setLocation(appointment.getLocation());

            calendarService.events().insert("primary", event).execute();

            appointment.setSynced_with_calendar(true);
            appointmentRepository.save(appointment);
        }
    }

}
