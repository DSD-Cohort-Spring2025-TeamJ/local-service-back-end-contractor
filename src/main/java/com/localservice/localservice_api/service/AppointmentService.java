package com.localservice.localservice_api.service;

import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> viewAllAppointments () {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> viewSingleAppointment (Long appointment_id) {
        return appointmentRepository.findById(appointment_id);
    }
}
