package com.localservice.localservice_api.services.model;

import com.localservice.localservice_api.appointments.model.AppointmentModel;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "services")
public class ServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long service_id;

    private String service_name;
    private int estimated_time;

    @OneToMany(mappedBy = "service_id")
    private List<AppointmentModel> appointments;

    public Long getService_id() {
        return service_id;
    }

    public void setService_id(Long service_id) {
        this.service_id = service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public int getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(int estimated_time) {
        this.estimated_time = estimated_time;
    }

    public List<AppointmentModel> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentModel> appointments) {
        this.appointments = appointments;
    }
}

