package com.localservice.localservice_api.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode
public class ServiceTechnicianId implements Serializable {

    private long tech_id;
    private long service_id;
}
