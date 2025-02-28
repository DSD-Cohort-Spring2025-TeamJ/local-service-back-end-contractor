package com.localservice.localservice_api.appointments.model;

import com.localservice.localservice_api.appointments.enums.AppointmentStatus;
import com.localservice.localservice_api.services.model.ServiceModel;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID appointment_id;

    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;
    private String description;
    private Instant estimated_time;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    private ServiceModel service_id;

    private String location;
    private String admin_note;
    private String client_note;

    public UUID getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(UUID appointment_id) {
        this.appointment_id = appointment_id;
    }

    public ZonedDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(ZonedDateTime created_at) {
        this.created_at = created_at;
    }

    public ZonedDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(ZonedDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(Instant estimated_time) {
        this.estimated_time = estimated_time;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public ServiceModel getService_id() {
        return service_id;
    }

    public void setService_id(ServiceModel service_id) {
        this.service_id = service_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAdmin_note() {
        return admin_note;
    }

    public void setAdmin_note(String admin_note) {
        this.admin_note = admin_note;
    }

    public String getClient_note() {
        return client_note;
    }

    public void setClient_note(String client_note) {
        this.client_note = client_note;
    }
}
