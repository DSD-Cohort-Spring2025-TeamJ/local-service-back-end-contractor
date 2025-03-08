package com.localservice.localservice_api.service;

import com.localservice.localservice_api.dto.AppointmentRequestDto;
import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.entity.Item;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.exceptions.ResourceNotFoundException;
import com.localservice.localservice_api.repository.AppointmentRepository;
import com.localservice.localservice_api.repository.ServiceRepository;
import com.localservice.localservice_api.repository.TechnicianRepository;
import jakarta.persistence.EntityNotFoundException;
import com.localservice.localservice_api.repository.ServiceItemRelationRepository;
import com.localservice.localservice_api.repository.ServiceTechinicianRelationRepository;
import com.localservice.localservice_api.response.AdminAppointmentViewDTO;
import com.localservice.localservice_api.response.ItemViewDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TechnicianRepository technicianRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceItemRelationRepository serviceItemRelationRepository;
    private final ServiceTechinicianRelationRepository serviceTechinicianRelationRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, TechnicianRepository technicianRepository, ServiceRepository serviceRepository, ServiceItemRelationRepository serviceItemRelationRepository, ServiceTechinicianRelationRepository serviceTechinicianRelationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRepository = serviceRepository;
        this.serviceItemRelationRepository = serviceItemRelationRepository;
        this.serviceTechinicianRelationRepository = serviceTechinicianRelationRepository;
    }

    @Transactional
    public Appointment createAppointment(AppointmentRequestDto request) {
        com.localservice.localservice_api.entity.Service service = serviceRepository.findById(request.getService_id())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        Technician technician = technicianRepository.findById(request.getTech_id())
                .orElseThrow(() -> new EntityNotFoundException("Technician not found"));

        Appointment appointment = getAppointment(request, service, technician);

        appointmentRepository.save(appointment);

        Map<String, List<String>> reservedSlots = technician.getReservedTimeSlots();
        reservedSlots.computeIfAbsent(request.getDate(), k -> new ArrayList<>()).add(request.getTime_slot());
        technician.setReservedTimeSlots(reservedSlots);
        technicianRepository.save(technician);

        return appointment;
    }

    private static Appointment getAppointment(AppointmentRequestDto request, com.localservice.localservice_api.entity.Service service, Technician technician) {
        Appointment appointment = new Appointment();
        appointment.setService_id(service);
        appointment.setClient_name(request.getName());
        appointment.setClient_email(request.getEmail());
        appointment.setClient_phone(request.getPhone());
        appointment.setLocation(request.getAddress());
        appointment.setIssue_description(request.getComment());

        String assignedTechnicians = String.valueOf(technician.getTech_id());
        appointment.setAssigned_technician_list(Collections.singletonList(assignedTechnicians));
        return appointment;
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
