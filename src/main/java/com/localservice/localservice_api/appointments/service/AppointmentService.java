package com.localservice.localservice_api.appointments.service;

import com.localservice.localservice_api.appointments.model.AppointmentModel;
import com.localservice.localservice_api.appointments.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public AppointmentModel createAppointment(AppointmentModel appointment) {
        return appointmentRepository.save(appointment);
    }

    public List<AppointmentModel> viewAllAppointments () {
        return appointmentRepository.findAll();
    }

    public Optional<AppointmentModel> viewSingleAppointment (UUID appointment_id) {
        return appointmentRepository.findById(appointment_id);
    }
}
