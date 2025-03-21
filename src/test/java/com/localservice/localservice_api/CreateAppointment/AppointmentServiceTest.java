package com.localservice.localservice_api.CreateAppointment;

import com.localservice.localservice_api.dto.AppointmentRequestDto;
import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.repository.AppointmentRepository;
import com.localservice.localservice_api.repository.ServiceRepository;
import com.localservice.localservice_api.repository.TechnicianRepository;
import com.localservice.localservice_api.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAppointment_AddsReservedSlotsAndSavesAppointment() {
        Service mockService = new Service();
        mockService.setEstimated_time(180);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));

        Technician technician = new Technician();
        technician.setTech_id(10L);
        technician.setReservedTimeSlots(new HashMap<>());

        when(technicianRepository.findById(10L)).thenReturn(Optional.of(technician));

        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setService_id(1L);
        request.setTech_id(10L);
        request.setDate("2025-03-22");
        request.setStart_time("9:00 AM");
        request.setEnd_time("12:00 PM");
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("1234567890");
        request.setAddress("123 Test Street");
        request.setComment("No issues.");

        System.out.println(">>> Starting appointment creation test with request: " + request);

        Appointment appointment = appointmentService.createAppointment(request);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        LocalDateTime expectedStart = LocalDateTime.parse("2025-03-22 9:00 AM", formatter);
        LocalDateTime expectedEnd = LocalDateTime.parse("2025-03-22 12:00 PM", formatter);

        System.out.println(">>> Verifying appointment start and end times...");
        assertEquals(expectedStart, appointment.getStart_time());
        assertEquals(expectedEnd, appointment.getEnd_time());
        assertEquals(Instant.ofEpochSecond(180 * 60L), appointment.getEstimated_time());
        assertEquals("John Doe", appointment.getClient_name());

        System.out.println(">>> Verifying reserved time slots for technician...");
        List<String> reserved = technician.getReservedTimeSlots().get("2025-03-22");
        System.out.println("Reserved slots saved for technician on 2025-03-22: " + reserved);
        assertEquals(List.of("9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM"), reserved);

        System.out.println(">>> Verifying repository interactions...");
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(technicianRepository, times(1)).save(any(Technician.class));

        System.out.println(">>> Test completed successfully.");
    }
}
