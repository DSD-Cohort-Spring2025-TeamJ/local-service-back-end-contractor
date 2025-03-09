package com.localservice.localservice_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localservice.localservice_api.dto.MultipleUpdateResponseDto;
import com.localservice.localservice_api.entity.Item;
import com.localservice.localservice_api.service.ItemService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
	
	@Autowired
    private ItemService itemService;

    // Create a new item
    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    // Retrieve all items
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // Retrieve an item by ID
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Item not found  with ID : " + itemId));
    }

    // Update an existing item
    @PutMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable long itemId, @RequestBody Item updatedItem) {
            Item updated = itemService.updateItem(itemId, updatedItem);
            return ResponseEntity.ok(updated);
    }
    
 // Update an existing item
    @PutMapping("/updateMultiple")
    public ResponseEntity<MultipleUpdateResponseDto<Item>> updateItems(@RequestBody List<Item> items) {
//    	itemService.updateMultipleItems(items);
         return ResponseEntity.ok(itemService.updateMultipleItems(items));
    }

    // Delete an item by ID
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
