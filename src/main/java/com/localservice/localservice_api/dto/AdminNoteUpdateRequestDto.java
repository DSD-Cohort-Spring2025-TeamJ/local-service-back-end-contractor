package com.localservice.localservice_api.dto;

import lombok.Data;

@Data
public class AdminNoteUpdateRequestDto {
    private Long appointment_id;
    private String admin_note;
}
