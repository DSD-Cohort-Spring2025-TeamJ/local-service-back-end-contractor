package com.localservice.localservice_api.TimeSlots;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.dto.ServiceTechnicianDto;
import com.localservice.localservice_api.repository.ServiceRepository;
import com.localservice.localservice_api.repository.TechnicianRepository;
import com.localservice.localservice_api.service.ServiceService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private ServiceService serviceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReservedSlotsProperlyUsedAcrossDates() {
        Service mockService = new Service();
        mockService.setEstimated_time(180);
        when(serviceRepository.findById(4L)).thenReturn(Optional.of(mockService));

        Technician tech = new Technician();
        tech.setTech_id(1L);

        Map<String, List<String>> reserved = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        reserved.put(today.toString(), Arrays.asList("10:30 AM", "11:00 AM", "12:00 PM", "12:30 PM", "1:00 PM"));
        reserved.put(tomorrow.toString(), Arrays.asList("9:30 AM", "10:00 AM", "11:30 AM"));

        tech.setReservedTimeSlots(reserved);

        when(technicianRepository.findAll()).thenReturn(List.of(tech));

        List<ServiceTechnicianDto> results = serviceService.getTimeSlotsBasedOnSelectedService(4L);

        for (ServiceTechnicianDto dto : results) {
            System.out.println("Checking tech: " + dto.getTechId() + " on date: " + dto.getDate());

            List<String> reservedSlots = reserved.getOrDefault(dto.getDate(), Collections.emptyList());
            System.out.println("Reserved slots for this date from test setup: " + reservedSlots);

            assertFalse(reservedSlots.isEmpty() && dto.getDate().equals(today.toString()),
                    "Expected reserved slots for date: " + dto.getDate() + ", but found none. Verify reservation mapping.");

            for (ServiceTechnicianDto.AvailabilityWindow window : dto.getAvailableWindows()) {
                System.out.println("Available window: Start = " + window.getStart() + ", End = " + window.getEnd() + ", Duration = " + window.getAvailableDuration());

                LocalTime start = LocalTime.parse(window.getStart(), DateTimeFormatter.ofPattern("h:mm a"));
                LocalTime end = LocalTime.parse(window.getEnd(), DateTimeFormatter.ofPattern("h:mm a"));

                for (String reservedSlot : reservedSlots) {
                    LocalTime reservedTime = LocalTime.parse(reservedSlot, DateTimeFormatter.ofPattern("h:mm a"));
                    assertTrue(reservedTime.isBefore(start) || reservedTime.isAfter(end),
                            "Conflict: Reserved time " + reservedSlot + " overlaps with available window " + window.getStart() + " - " + window.getEnd() + " on date: " + dto.getDate());
                }
            }
        }
    }
}
