package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.dto.AppointmentRequestDto;
import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.response.AdminAppointmentViewDTO;
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
    public ResponseEntity<Appointment> createAppointment (@RequestBody AppointmentRequestDto appointmentRequestDto) {
            Appointment savedAppointment = appointmentService.createAppointment(appointmentRequestDto);
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

    @GetMapping("/admin/{appointment_id}")
    public ResponseEntity<?> viewAllAppointmentsAdmin (@PathVariable Long appointment_id) {
        try {
            AdminAppointmentViewDTO appointment = appointmentService.viewAdminViewAppointment(appointment_id);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving admin appointment view: " + e.getMessage());
        }
    }

}
