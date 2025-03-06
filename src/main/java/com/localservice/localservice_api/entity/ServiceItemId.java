package com.localservice.localservice_api.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode
public class ServiceItemId implements Serializable {

    private long service_id;
    private long item_id;
}
