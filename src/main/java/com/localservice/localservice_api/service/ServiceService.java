package com.localservice.localservice_api.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

	public List<ServiceTechnicianDto> getTimeSlotsBasedOnSelectedService(Long serviceId) {
		Map<String, List<String>> allAvailableTimeSlots = generateDateAndTimeSlotsWindow();
		Map<Long, Map<String, List<String>>> reservedTimeSlots = getTechniciansReservedTimeSlots();
		int estimatedTime = getEstimatedTimeBasedOnService(serviceId);

        List<ServiceTechnicianDto> results = new ArrayList<>();

		reservedTimeSlots.forEach((techId, reservedPerDate) -> {
			allAvailableTimeSlots.forEach((date, dailySlots) -> {
				List<String> filteredSlots = dailySlots.stream()
						.filter(slot -> !reservedPerDate.getOrDefault(date, Collections.emptyList()).contains(slot))
						.collect(Collectors.toList());

				List<ServiceTechnicianDto.AvailabilityWindow> availabilityWindows = findAvailabilityWindows(filteredSlots, estimatedTime);

				if (!availabilityWindows.isEmpty()) {
					results.add(new ServiceTechnicianDto(techId, date, availabilityWindows));
				}
			});
		});

		return results;
	}

	private int getEstimatedTimeBasedOnService(long serviceId) {
		Service service = serviceRepository.findById(serviceId)
				.orElseThrow(() -> new EntityNotFoundException("No Service found with that ID"));
		return service.getEstimated_time();
	}

	private Map<Long, Map<String, List<String>>> getTechniciansReservedTimeSlots() {
		List<Technician> technicians = technicianRepository.findAll();
		return technicians.stream().collect(Collectors.toMap(Technician::getTech_id, Technician::getReservedTimeSlots));
	}

	private Map<String, List<String>> generateDateAndTimeSlotsWindow() {
		Map<String, List<String>> dateTimeMap = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

		for (int i = 0; i < 7; i++) {
			LocalDate date = LocalDate.now().plusDays(i);
			String dateString = date.toString();

			List<String> timeSlots = new ArrayList<>();
			LocalTime startTime = LocalTime.of(9, 0);
			LocalTime endTime = LocalTime.of(17, 0);

			while (startTime.isBefore(endTime)) {
				timeSlots.add(startTime.format(formatter));
				startTime = startTime.plusMinutes(30);
			}

			dateTimeMap.put(dateString, timeSlots);
		}
		return dateTimeMap;
	}

	private boolean areConsecutive(LocalTime first, LocalTime second) {
		return first.plusMinutes(30).equals(second);
	}

	private List<ServiceTechnicianDto.AvailabilityWindow> findAvailabilityWindows(List<String> availableSlots, int estimatedTimeInMinutes) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
		List<LocalTime> times = availableSlots.stream()
				.map(slot -> LocalTime.parse(slot, formatter))
				.sorted()
				.toList();

		int requiredSlots = estimatedTimeInMinutes / 30;
		List<ServiceTechnicianDto.AvailabilityWindow> windows = new ArrayList<>();

		for (int i = 0; i <= times.size() - requiredSlots; i++) {
			boolean fits = true;
			for (int j = 1; j < requiredSlots; j++) {
				if (!areConsecutive(times.get(i + j - 1), times.get(i + j))) {
					fits = false;
					break;
				}
			}
			if (fits) {
				LocalTime start = times.get(i);
				LocalTime end = start.plusMinutes(estimatedTimeInMinutes);
				windows.add(new ServiceTechnicianDto.AvailabilityWindow(start.format(formatter), end.format(formatter), estimatedTimeInMinutes));
			}
		}

		return windows;
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
