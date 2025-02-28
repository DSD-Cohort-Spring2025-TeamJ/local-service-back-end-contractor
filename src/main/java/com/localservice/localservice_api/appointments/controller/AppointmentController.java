package com.localservice.localservice_api.appointments.controller;

import com.localservice.localservice_api.appointments.model.AppointmentModel;
import com.localservice.localservice_api.appointments.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/service-provider/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentModel createAppointment (@RequestBody AppointmentModel appointment) {
        return appointmentService.createAppointment(appointment);
    }

    @GetMapping
    public List<AppointmentModel> viewAllAppointments () {
        return appointmentService.viewAllAppointments();
    }

    @GetMapping("/{appointment_id}")
    public Optional<AppointmentModel> viewSingleAppointment (@PathVariable Long appointment_id) {
        return appointmentService.viewSingleAppointment(appointment_id);
    }
}
