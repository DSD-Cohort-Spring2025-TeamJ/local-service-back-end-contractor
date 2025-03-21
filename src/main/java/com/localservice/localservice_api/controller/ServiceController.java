package com.localservice.localservice_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localservice.localservice_api.dto.MultipleUpdateResponseDto;
import com.localservice.localservice_api.dto.ServiceTechnicianDto;
import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.service.ServiceService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }
    
 // Create a new item
    @PostMapping
    public Service createItem(@RequestBody Service service) {
        return serviceService.createService(service);
    }

    // Retrieve an item by ID
    @GetMapping("/{serviceId}")
    public ResponseEntity<Service> getItemById(@PathVariable long serviceId) {
        return serviceService.getServiceById(serviceId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Service not found  with ID : " + serviceId));
    }

    // Update an existing item
    @PutMapping("/{serviceId}")
    public ResponseEntity<Service> updateItem(@PathVariable long serviceId, @RequestBody Service updatedService) {
            Service updated = serviceService.updateService(serviceId, updatedService);
            return ResponseEntity.ok(updated);
    }
    
 // Update an existing item
    @PutMapping("/updateMultiple")
    public ResponseEntity<MultipleUpdateResponseDto<Service>> updateItems(@RequestBody List<Service> services) {
         return ResponseEntity.ok(serviceService.updateMultipleServices(services));
    }

    // Delete an item by ID
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteItem(@PathVariable long serviceId) {
        serviceService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
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
            List<ServiceTechnicianDto> serviceTechnicianDto = serviceService.getTimeSlotsBasedOnSelectedService(service_id);

            if (serviceTechnicianDto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No availability found for service ID: " + service_id);
            }

            return ResponseEntity.ok(serviceTechnicianDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving time slots for service ID " + service_id + ": " + e.getMessage());
        }
    }
}
