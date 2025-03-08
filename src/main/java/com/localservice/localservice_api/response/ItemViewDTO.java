package com.localservice.localservice_api.response;

import com.localservice.localservice_api.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemViewDTO {
    private Item item;
    private int qty_needed;
}
