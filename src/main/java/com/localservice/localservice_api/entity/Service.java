package com.localservice.localservice_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long service_id;

    private String service_name;
    private int estimated_time;

    @OneToMany(mappedBy = "service_id")
    private List<Appointment> appointments;

}

