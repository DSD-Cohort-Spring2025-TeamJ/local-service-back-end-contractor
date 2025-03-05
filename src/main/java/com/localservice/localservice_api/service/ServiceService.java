package com.localservice.localservice_api.service;

import com.localservice.localservice_api.entity.Service;
import com.localservice.localservice_api.repository.ServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getServiceList () {
        return serviceRepository.findAll();
    }
}
