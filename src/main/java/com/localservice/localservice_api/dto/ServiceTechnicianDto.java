package com.localservice.localservice_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ServiceTechnicianDto {
    private long techId;
    private String date;
    private List<AvailabilityWindow> availableWindows;

    @Data
    @AllArgsConstructor
    public static class AvailabilityWindow {
        private String start;
        private String end;
        private int availableDuration;
    }
}
