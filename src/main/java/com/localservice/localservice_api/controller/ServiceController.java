package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.dto.ServiceTechnicianDto;
import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.service.ServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public ResponseEntity<?> getServiceList() {
        try {
            List<Service> serviceEntities = serviceService.getServiceList();
            return ResponseEntity.ok(serviceEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving service list: " + e.getMessage());
        }
    }

    @GetMapping("/{service_id}/timeSlots")
    public ResponseEntity<?> getTimeSlotsBasedOnSelectedService(@PathVariable long service_id) {
        try {
            ServiceTechnicianDto serviceTechnicianDto = serviceService.getTimeSlotsBasedOnSelectedService(service_id);

            if (serviceTechnicianDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No service found with ID: " + service_id);
            }

            return ResponseEntity.ok(serviceTechnicianDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving time slots for service ID " + service_id + ": " + e.getMessage());
        }
    }
}
