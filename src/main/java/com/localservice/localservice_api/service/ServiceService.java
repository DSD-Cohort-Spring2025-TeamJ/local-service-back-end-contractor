package com.localservice.localservice_api.service;

import com.localservice.localservice_api.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<com.localservice.localservice_api.entity.Service> getServiceList () {
        return serviceRepository.findAll();
    }
}
