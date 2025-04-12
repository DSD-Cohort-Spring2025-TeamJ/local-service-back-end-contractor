package com.localservice.localservice_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Data
@Entity
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tech_id;

    private String name;
    private int hourly_rate; // TODO camel case. Hibernate will translate to snake case
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<String>> reservedTimeSlots;

}
