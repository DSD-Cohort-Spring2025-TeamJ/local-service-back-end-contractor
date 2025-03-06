package com.localservice.localservice_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service_item_relation")
public class ServiceItemRelation {

    @EmbeddedId
    private ServiceItemId serviceItemId;

    @ManyToOne
    @MapsId("service_id")
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @MapsId("item_id")
    @JoinColumn(name = "item_id")
    private Item item;

    private int qty_needed;
}
