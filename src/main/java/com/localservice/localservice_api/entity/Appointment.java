package com.localservice.localservice_api.entity;

import com.localservice.localservice_api.constants.Constants;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long appointment_id;

    @CreationTimestamp
    private Instant created_at;
    @UpdateTimestamp
    private Instant updated_at;
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
