package com.localservice.localservice_api.dto;

import lombok.Data;

@Data
public class AppointmentRequestDto {
    private Long service_id;
    private int estimated_time;
    private Long tech_id;
    private String date;
    private String time_slot;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String comment;
}
