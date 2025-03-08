package com.localservice.localservice_api.response;

import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.entity.Technician;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminAppointmentViewDTO {
    private Appointment appointment;
    private List<Technician> technicians;
    private List<ItemViewDTO> items;
}
