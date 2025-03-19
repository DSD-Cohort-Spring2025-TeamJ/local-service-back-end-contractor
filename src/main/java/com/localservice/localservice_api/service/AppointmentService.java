package com.localservice.localservice_api.service;

import com.localservice.localservice_api.constants.Constants;
import com.localservice.localservice_api.dto.AdminNoteUpdateRequestDto;
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

import java.time.format.DateTimeFormatter;
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

    public Appointment updateAppointmentStatus(Long id, String incomingStatus) {
        Constants status;
        try {
            status = Constants.valueOf(incomingStatus.toUpperCase());
            if (status.equals(Constants.ACCEPTED)) {
                sendClientAcceptedEmail(id);
                sendTechnicianAssignedEmail(id);
            }
            if (status.equals(Constants.REJECTED)) {
                releaseTimeSlotBackToAvailable(id);
                sendClientRejectedEmail(id);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided: " + incomingStatus +
                    " Allowed values: PENDING, ASSIGNED, COMPLETED, REJECTED, ACTIVE.");
        }

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
                    int qtyNeeded = serviceItemRelationRepository.getQtyNeededByItemid(item.getItem_id(), serviceId);
                    boolean isOutOfStock = item.getStock_qty() < qtyNeeded;
                    return new ItemViewDTO(item, qtyNeeded, isOutOfStock);
                })
                .collect(Collectors.toList());

        List<Technician> technicians = serviceTechinicianRelationRepository.getTechiciansByService_id(serviceId);
        if (technicians.size() == 1) {
            return new AdminAppointmentViewDTO(appointment, Collections.singletonList(technicians.get(0)), itemViews);
        }
        return new AdminAppointmentViewDTO(appointment, technicians, itemViews);
    }

    public String updateItemInventoryAndNotify(Long appointment_id) throws MessagingException {
        AdminAppointmentViewDTO adminAppointmentViewDTO = viewAdminViewAppointment(appointment_id);
        List<ItemViewDTO> itemViewDTOList = adminAppointmentViewDTO.getItems();
        // check all items in itemViewDTOList and if any item is out of stock, mark isOutOfStock as true
        boolean isOutOfStock = itemViewDTOList.stream().anyMatch(ItemViewDTO::isOutOfStock);

        if (!isOutOfStock) {
            return "Invalid request";
        }

        sendOutOfStockEmail(appointment_id);

        return "Inventory has been updated successfully";
    }

    public String updateAdminNote(AdminNoteUpdateRequestDto requestDto) {
        Appointment appointment = appointmentRepository.findById(requestDto.getAppointment_id())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + requestDto.getAppointment_id()));

        if(requestDto.getAdmin_note() != null) {
            appointment.setAdmin_note(requestDto.getAdmin_note());
            appointmentRepository.save(appointment);
        }

        return "Admin Note successfully saved";
    }

    private void releaseTimeSlotBackToAvailable(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        String techId = appointment.getAssigned_technician_list()
                .stream()
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("No assigned tech to apt with id " + id));

        Technician technician = technicianRepository.findById(Long.valueOf(techId))
                .orElseThrow(() -> new ResourceNotFoundException("tech not found with id " + techId));


        String dateKey = appointment.getStart_time().toLocalDate().toString();
        String timeSlot = appointment.getStart_time().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"));

        if (technician.getReservedTimeSlots().containsKey(dateKey)) {
            technician.getReservedTimeSlots().get(dateKey).removeIf(time -> time.equals(timeSlot));
        }

        technicianRepository.save(technician);
    }

    private void sendClientStatusEmail(Long appointmentId, String statusMessage, String subjectPrefix, boolean bccAdmin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        String emailBody = generateAppointmentEmailBody(appointment, statusMessage);
        sendEmail(appointment.getClient_email(), subjectPrefix + appointmentId, emailBody, bccAdmin);
    }

    private void sendClientAcceptedEmail(Long appointmentId) {
        sendClientStatusEmail(appointmentId, "Accepted", "Your appointment was accepted! ", false);
    }

    private void sendClientRejectedEmail(Long appointmentId) {
        sendClientStatusEmail(appointmentId, "Rejected", "Your appointment was rejected! ", true);
    }

    private void sendOutOfStockEmail(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        String emailBody = generateAppointmentEmailBody(appointment, "Items Out of Stock");
        sendEmail("service_provider@gmail.com", "Items out of stock for Appointment " + appointmentId, emailBody, false);
    }

    private void sendTechnicianAssignedEmail(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        List<Item> items = serviceItemRelationRepository.getItemsByService_id(appointment.getService_id().getService_id());

        List<ItemViewDTO> itemViews = items.stream()
                .map(item -> {
                    int qtyNeeded = serviceItemRelationRepository.getQtyNeededByItemid(item.getItem_id(), appointment.getService_id().getService_id());
                    boolean outOfStock = item.getStock_qty() < qtyNeeded;
                    return new ItemViewDTO(item, qtyNeeded, outOfStock);
                })
                .collect(Collectors.toList());

        boolean hasOutOfStock = itemViews.stream().anyMatch(ItemViewDTO::isOutOfStock);
        Technician technician = getAssignedTechnician(appointment);

        String emailBody = generateTechnicianEmailBody(appointment, itemViews, hasOutOfStock);
        String subject = "Appointment Assigned: " + appointmentId + (hasOutOfStock ? " (Items Out of Stock Alert)" : "");

        sendEmail(technician.getEmail(), subject, emailBody, false);
    }

    private void sendEmail(String to, String subject, String body, boolean bccAdmin) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("pragmatic_plumber@gmail.com");
            helper.setTo(to);
            if (bccAdmin) {
                helper.setBcc("pragmatic_plumber@gmail.com");
            }
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String generateAppointmentEmailBody(Appointment appointment, String statusMessage) {
        return "<html><body>" +
                "<h2>Appointment Details</h2>" +
                "<p><strong>Appointment ID:</strong> " + appointment.getAppointment_id() + "</p>" +
                "<p><strong>Client Name:</strong> " + appointment.getClient_name() + "</p>" +
                "<p><strong>Client Phone:</strong> " + appointment.getClient_phone() + "</p>" +
                "<p><strong>Start Time:</strong> " + appointment.getStart_time() + "</p>" +
                "<p><strong>End Time:</strong> " + appointment.getEnd_time() + "</p>" +
                "<p><strong>Issue Description:</strong> " + appointment.getIssue_description() + "</p>" +
                "<p><strong>Status:</strong> " + statusMessage + "</p>" +
                "<br><p>Thank you,</p>" +
                "<p>Your Pragmatic Plumber Team</p>" +
                "</body></html>";
    }

    private Technician getAssignedTechnician(Appointment appointment) {
        String techId = appointment.getAssigned_technician_list()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No technician assigned to appointment " + appointment.getAppointment_id()));

        return technicianRepository.findById(Long.valueOf(techId))
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + techId));
    }

    private String generateTechnicianEmailBody(Appointment appointment, List<ItemViewDTO> items, boolean hasOutOfStock) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>New Appointment Assigned</h2>");
        sb.append("<p><strong>Appointment ID:</strong> ").append(appointment.getAppointment_id()).append("</p>");
        sb.append("<p><strong>Client Name:</strong> ").append(appointment.getClient_name()).append("</p>");
        sb.append("<p><strong>Client Phone:</strong> ").append(appointment.getClient_phone()).append("</p>");
        sb.append("<p><strong>Location:</strong> ").append(appointment.getLocation()).append("</p>");
        sb.append("<p><strong>Start Time:</strong> ").append(appointment.getStart_time()).append("</p>");
        sb.append("<p><strong>End Time:</strong> ").append(appointment.getEnd_time()).append("</p>");
        sb.append("<p><strong>Issue Description:</strong> ").append(appointment.getIssue_description()).append("</p>");

        sb.append("<h3>Items Needed:</h3><ul>");
        for (ItemViewDTO itemView : items) {
            sb.append("<li>").append(itemView.getItem().getItem_name())
                    .append(" — Needed: ").append(itemView.getQty_needed())
                    .append(" — In Stock: ").append(itemView.getItem().getStock_qty());
            if (itemView.isOutOfStock()) {
                sb.append(" <strong>(OUT OF STOCK)</strong>");
            }
            sb.append("</li>");
        }
        sb.append("</ul>");

        if (hasOutOfStock) {
            sb.append("<p><strong>⚠ Some parts are out of stock. Please check inventory before proceeding.</strong></p>");
        }

        sb.append("<br><p>Thank you,</p>");
        sb.append("<p>Your Pragmatic Plumber Team</p>");
        sb.append("</body></html>");

        return sb.toString();
    }

}
