package com.localservice.localservice_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ServiceTechnicianDto {
    private long service_id;
    private int estimated_time;
    private Map<Long, Map<String, List<String>>> availableTimeSlotsByTechnician;

}
