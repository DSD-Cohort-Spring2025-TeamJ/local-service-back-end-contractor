package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment (@RequestBody Appointment appointment) {
            Appointment savedAppointment = appointmentService.createAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> viewAllAppointments () {
        List<Appointment> appointments = appointmentService.viewAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{appointment_id}")
    public ResponseEntity<Optional<Appointment>> viewSingleAppointment (@PathVariable Long appointment_id) {
        Optional<Appointment> appointment = appointmentService.viewSingleAppointment(appointment_id);
        return ResponseEntity.ok(appointment);
    }
}
