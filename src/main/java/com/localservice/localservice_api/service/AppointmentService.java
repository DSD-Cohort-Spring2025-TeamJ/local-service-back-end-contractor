package com.localservice.localservice_api.service;

import com.localservice.localservice_api.constants.Constants;
import com.localservice.localservice_api.dto.AppointmentRequestDto;
import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.entity.Item;
import com.localservice.localservice_api.exceptions.ResourceNotFoundException;
import com.localservice.localservice_api.repository.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import com.localservice.localservice_api.response.AdminAppointmentViewDTO;
import com.localservice.localservice_api.response.ItemViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private final ItemRepository itemRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    public AppointmentService(AppointmentRepository appointmentRepository, TechnicianRepository technicianRepository, ServiceRepository serviceRepository, ServiceItemRelationRepository serviceItemRelationRepository, ServiceTechinicianRelationRepository serviceTechinicianRelationRepository, ItemRepository itemRepository) {
        this.appointmentRepository = appointmentRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRepository = serviceRepository;
        this.serviceItemRelationRepository = serviceItemRelationRepository;
        this.serviceTechinicianRelationRepository = serviceTechinicianRelationRepository;
        this.itemRepository = itemRepository;
    }

    public Appointment updateAppointmentStatus(Long id, Constants status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointment.setStatus(status);

        return appointmentRepository.save(appointment);
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
                    boolean isOutOfStock = item.getStock_qty() < qtyNeeded;
                    return new ItemViewDTO(item, qtyNeeded, isOutOfStock);
                })
                .collect(Collectors.toList());

        List<Technician> technicians = serviceTechinicianRelationRepository.getTechiciansByService_id(serviceId);

        return new AdminAppointmentViewDTO(appointment, technicians, itemViews);
    }

    public String updateItemInventoryAndNotify(Long appointment_id) throws MessagingException {
        AdminAppointmentViewDTO adminAppointmentViewDTO = viewAdminViewAppointment(appointment_id);
        List<ItemViewDTO> itemViewDTOList = adminAppointmentViewDTO.getItems();
        boolean isOutOfStock = updateInventory(itemViewDTOList);

        if (!isOutOfStock) {
            return "Invalid request";
        }

        sendOutOfStockEmail(appointment_id);

        return "Inventory has been updated successfully";
    }

    private boolean updateInventory(List<ItemViewDTO> itemViewDTOList) {
        boolean isOutOfStock = false;

        for (ItemViewDTO itemViewDTO : itemViewDTOList) {
            if (itemViewDTO.isOutOfStock()) {
                Item item = itemViewDTO.getItem();
                item.setStock_qty(itemViewDTO.getQty_needed());
                itemRepository.save(item);
                isOutOfStock = true;
            }
        }
        return isOutOfStock;
    }


    private void sendOutOfStockEmail(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        String emailBody = generateEmailBody(appointment);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("pragmatic_plumber@gmail.com");
            helper.setTo("service_provider@gmail.com");
            helper.setSubject("Items out of stock for Appointment " + appointmentId);
            helper.setText(emailBody, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send out-of-stock email: " + e.getMessage(), e);
        }
    }

    private String generateEmailBody(Appointment appointment) {
        return "<html><body>" +
                "<h2>Appointment Details</h2>" +
                "<p><strong>Appointment ID:</strong> " + appointment.getAppointment_id() + "</p>" +
                "<p><strong>Client Name:</strong> " + appointment.getClient_name() + "</p>" +
                "<p><strong>Client Phone:</strong> " + appointment.getClient_phone() + "</p>" +
                "<p><strong>Start Time:</strong> " + appointment.getStart_time() + "</p>" +
                "<p><strong>End Time:</strong> " + appointment.getEnd_time() + "</p>" +
                "<p><strong>Issue Description:</strong> " + appointment.getIssue_description() + "</p>" +
                "<p><strong>Estimated Time:</strong> " + appointment.getEstimated_time() + "</p>" +
                "<p><strong>Status:</strong> Items Out of Stock</p>" +
                "<br><p>Thank you,</p>" +
                "<p>Your Pragmatic Plumber Team</p>" +
                "</body></html>";
    }
}
