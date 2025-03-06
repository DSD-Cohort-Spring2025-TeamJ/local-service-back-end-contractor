package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<Service>> getServiceList () {
        List<Service> serviceEntities = serviceService.getServiceList();
        return ResponseEntity.ok(serviceEntities);
    }
}
