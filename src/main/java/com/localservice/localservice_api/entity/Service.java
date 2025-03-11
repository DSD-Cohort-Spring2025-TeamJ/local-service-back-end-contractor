package com.localservice.localservice_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long service_id;

    private String service_name;
    private int estimated_time;

}

