package com.localservice.localservice_api.service;

import com.localservice.localservice_api.dto.ServiceTechnicianDto;
import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.repository.ServiceRepository;
import com.localservice.localservice_api.repository.TechnicianRepository;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final TechnicianRepository technicianRepository;

    public ServiceService(ServiceRepository serviceRepository, TechnicianRepository technicianRepository) {
        this.serviceRepository = serviceRepository;
        this.technicianRepository = technicianRepository;
    }

    public List<Service> getServiceList () {
        return serviceRepository.findAll();
    }

    public ServiceTechnicianDto getTimeSlotsBasedOnSelectedService(Long service_id) {
        Map<String, List<String>> availableTimeSlots = generateDateAndTimeSlotsWindow();
        Map<Long, Map<String, List<String>>> reservedTimeSlots = getTechniciansReservedTimeSlots();
        int estimatedTime = getEstimatedTimeBasedOnService(service_id);

        Map<Long, Map<String, List<String>>> availableSlotsByTechnician = new HashMap<>();

        reservedTimeSlots.forEach((techId, techReservedSlots) -> {
            Map<String, Set<String>> flattenedReservedSlots = techReservedSlots.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new HashSet<>(e.getValue())
                    ));

            Map<String, List<String>> techAvailableSlots = availableTimeSlots.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
                    techAvailableSlots.forEach((date, slots) ->
                    slots.removeIf(time -> flattenedReservedSlots.getOrDefault(date, Collections.emptySet()).contains(time))
            );
            availableSlotsByTechnician.put(techId, techAvailableSlots);
        });

        return new ServiceTechnicianDto(service_id, estimatedTime, availableSlotsByTechnician);
    }

    private int getEstimatedTimeBasedOnService (long service_id) {
        Service service = serviceRepository.findById(service_id)
                .orElseThrow(() -> new EntityNotFoundException("No Service found with that ID"));
        return service.getEstimated_time();
    }

    private Map<Long, Map<String, List<String>>> getTechniciansReservedTimeSlots () {
        List<Technician> technicians = technicianRepository.findAll();
        return technicians.stream()
                .collect(Collectors.toMap(
                        Technician::getTech_id,
                        Technician::getReservedTimeSlots
                ));
    }

    private Map<String, List<String>> generateDateAndTimeSlotsWindow () {
        Map<String, List<String>> dateTimeMap = new HashMap<>();

        for(int i = 0; i < 7; i++ ) {
            LocalDate todayPlusSevenDays = LocalDate.now().plusDays(i);
            String dateString = todayPlusSevenDays.toString();

            List<String> timeSlots = new ArrayList<>(Arrays.asList(
                    "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                    "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM"
            ));

            dateTimeMap.put(dateString, timeSlots);
        }
        return dateTimeMap;
    }

}
