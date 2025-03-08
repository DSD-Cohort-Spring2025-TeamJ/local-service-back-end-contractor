package com.localservice.localservice_api.entity;

import com.localservice.localservice_api.constants.Constants;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long appointment_id;

    private String client_name;
    private String client_email;
    private String client_phone;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String issue_description;
    private Instant estimated_time;

    @Enumerated(EnumType.STRING)
    private Constants status;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    private Service service_id;

    private String location;
    private String admin_note;
    private String assigned_technician_list;
    private double quoted_price;

    @Transient
    private List<String> missing_item_list;

}
