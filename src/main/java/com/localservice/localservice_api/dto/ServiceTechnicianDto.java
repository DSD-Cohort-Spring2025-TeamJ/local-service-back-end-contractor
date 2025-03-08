package com.localservice.localservice_api.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ServiceTechnicianDto {
    private long service_id;
    private int estimated_time;
    private Map<Long, Map<String, List<String>>> availableTimeSlotsByTechnician;

    public ServiceTechnicianDto(Long serviceId, int estimatedTime, Map<Long, Map<String, List<String>>> availableSlotsByTechnician) {
        this.service_id = serviceId;
        this.estimated_time = estimatedTime;
        this.availableTimeSlotsByTechnician = availableSlotsByTechnician;
    }
}
