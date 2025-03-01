package com.localservice.localservice_api.entity;

import com.localservice.localservice_api.constants.Constants;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;

@Data
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long appointment_id;

    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;
    private String description;
    private Instant estimated_time;

    @Enumerated(EnumType.STRING)
    private Constants status;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    private Service service_id;

    private String location;
    private String admin_note;
    private String client_note;

}
