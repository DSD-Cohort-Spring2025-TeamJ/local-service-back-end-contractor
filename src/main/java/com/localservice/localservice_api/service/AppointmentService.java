package com.localservice.localservice_api.service;

import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.entity.Item;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.exceptions.ResourceNotFoundException;
import com.localservice.localservice_api.repository.AppointmentRepository;
import com.localservice.localservice_api.repository.ServiceItemRelationRepository;
import com.localservice.localservice_api.repository.ServiceTechinicianRelationRepository;
import com.localservice.localservice_api.response.AdminAppointmentViewDTO;
import com.localservice.localservice_api.response.ItemViewDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRelationRepository serviceItemRelationRepository;
    private final ServiceItemRelationRepository sirServiceItemRelationRepository;
    private final ServiceTechinicianRelationRepository serviceTechinicianRelationRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ServiceItemRelationRepository serviceItemRelationRepository
    , ServiceItemRelationRepository sirServiceItemRelationRepository, ServiceTechinicianRelationRepository serviceTechinicianRelationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceItemRelationRepository = serviceItemRelationRepository;
        this.sirServiceItemRelationRepository = sirServiceItemRelationRepository;
        this.serviceTechinicianRelationRepository = serviceTechinicianRelationRepository;
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

    public AdminAppointmentViewDTO viewAdminViewAppointment(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        Long serviceId = appointment.getService_id().getService_id();

        List<Item> items = serviceItemRelationRepository.getItemsByService_id(serviceId);

        List<ItemViewDTO> itemViews = items.stream()
                .map(item -> {
                    int qtyNeeded = serviceItemRelationRepository.getQtyNeededByItemid(item.getItem_id());
                    return new ItemViewDTO(item, qtyNeeded);
                })
                .collect(Collectors.toList());

        List<Technician> technicians = serviceTechinicianRelationRepository.getTechiciansByService_id(serviceId);

        return new AdminAppointmentViewDTO(appointment, technicians, itemViews);
    }
}
