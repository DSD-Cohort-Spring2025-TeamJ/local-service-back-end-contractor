package com.localservice.localservice_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tech_id;

    private String name;
    private int hourly_rate;
    private String reserved_time_slots;
}
