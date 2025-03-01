package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public Appointment createAppointment (@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    @GetMapping
    public List<Appointment> viewAllAppointments () {
        return appointmentService.viewAllAppointments();
    }

    @GetMapping("/{appointment_id}")
    public Optional<Appointment> viewSingleAppointment (@PathVariable Long appointment_id) {
        return appointmentService.viewSingleAppointment(appointment_id);
    }
}
