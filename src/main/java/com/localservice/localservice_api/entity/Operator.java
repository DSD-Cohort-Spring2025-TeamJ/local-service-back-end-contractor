package com.localservice.localservice_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long user_id;

    private String name;
    private String email;
    private String phone;
}
