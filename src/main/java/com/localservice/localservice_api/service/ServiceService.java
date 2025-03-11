package com.localservice.localservice_api.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.localservice.localservice_api.dto.MultipleUpdateResponseDto;
import com.localservice.localservice_api.dto.ServiceTechnicianDto;
import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.entity.Technician;
import com.localservice.localservice_api.repository.ServiceRepository;
import com.localservice.localservice_api.repository.TechnicianRepository;

import jakarta.persistence.EntityNotFoundException;

@org.springframework.stereotype.Service
public class ServiceService {
	private final ServiceRepository serviceRepository;
	private final TechnicianRepository technicianRepository;

	public ServiceService(ServiceRepository serviceRepository, TechnicianRepository technicianRepository) {
		this.serviceRepository = serviceRepository;
		this.technicianRepository = technicianRepository;
	}

	public List<Service> getServiceList() {
		return serviceRepository.findAll();
	}

	public ServiceTechnicianDto getTimeSlotsBasedOnSelectedService(Long service_id) {
		Map<String, List<String>> availableTimeSlots = generateDateAndTimeSlotsWindow();
		Map<Long, Map<String, List<String>>> reservedTimeSlots = getTechniciansReservedTimeSlots();
		int estimatedTime = getEstimatedTimeBasedOnService(service_id);

		Map<Long, Map<String, List<String>>> availableSlotsByTechnician = new HashMap<>();

		reservedTimeSlots.forEach((techId, techReservedSlots) -> {
			Map<String, Set<String>> flattenedReservedSlots = techReservedSlots.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue())));

			Map<String, List<String>> techAvailableSlots = availableTimeSlots.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
			techAvailableSlots.forEach((date, slots) -> slots.removeIf(
					time -> flattenedReservedSlots.getOrDefault(date, Collections.emptySet()).contains(time)));
			availableSlotsByTechnician.put(techId, techAvailableSlots);
		});

		return new ServiceTechnicianDto(service_id, estimatedTime, availableSlotsByTechnician);
	}

	private int getEstimatedTimeBasedOnService(long service_id) {
		Service service = serviceRepository.findById(service_id)
				.orElseThrow(() -> new EntityNotFoundException("No Service found with that ID"));
		return service.getEstimated_time();
	}

	private Map<Long, Map<String, List<String>>> getTechniciansReservedTimeSlots() {
		List<Technician> technicians = technicianRepository.findAll();
		return technicians.stream().collect(Collectors.toMap(Technician::getTech_id, Technician::getReservedTimeSlots));
	}

	private Map<String, List<String>> generateDateAndTimeSlotsWindow() {
		Map<String, List<String>> dateTimeMap = new HashMap<>();

		for (int i = 0; i < 7; i++) {
			LocalDate todayPlusSevenDays = LocalDate.now().plusDays(i);
			String dateString = todayPlusSevenDays.toString();

			List<String> timeSlots = new ArrayList<>(Arrays.asList("9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
					"1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM"));

			dateTimeMap.put(dateString, timeSlots);
		}
		return dateTimeMap;
	}

	public Service createService(Service service) {
		return serviceRepository.save(service);
	}

	public Optional<Service> getServiceById(long serviceId) {
		return serviceRepository.findById(serviceId);
	}

	public Service updateService(long serviceId, Service updatedService) {
		return getServiceById(serviceId).map(service -> {
			service.setService_name(updatedService.getService_name());
			service.setEstimated_time(updatedService.getEstimated_time());
			return serviceRepository.save(service);
		}).orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + serviceId));
	}

	public MultipleUpdateResponseDto<Service> updateMultipleServices(List<Service> services) {
		MultipleUpdateResponseDto<Service> res = new MultipleUpdateResponseDto<Service>();
		services.stream().map(service -> {
			try {
				updateService(service.getService_id(), service);
				res.getUpdated().add(service);
				return service.getService_id();
			} catch (Exception e) {
				res.getNotUpdated().add(service);
				return service.getService_id();
			}
		}).collect(Collectors.toList());
		return res;
	}

	public void deleteService(long serviceId) {
		getServiceById(serviceId)
				.orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + serviceId));
		serviceRepository.deleteById(serviceId);
	}

}
