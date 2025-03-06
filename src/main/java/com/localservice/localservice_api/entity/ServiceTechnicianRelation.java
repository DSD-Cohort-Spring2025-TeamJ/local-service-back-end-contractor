package com.localservice.localservice_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service_tech_relation")
public class ServiceTechnicianRelation {

    @EmbeddedId
    private ServiceTechnicianId serviceTechnicianId;

    @ManyToOne
    @MapsId("tech_id")
    @JoinColumn(name = "tech_id")
    private Technician technician;

    @ManyToOne
    @MapsId("service_id")
    @JoinColumn(name = "service_id")
    private Service service;
}
