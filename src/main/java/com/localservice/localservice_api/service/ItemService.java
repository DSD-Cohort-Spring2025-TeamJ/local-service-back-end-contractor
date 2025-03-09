package com.localservice.localservice_api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.localservice.localservice_api.dto.MultipleUpdateResponseDto;
import com.localservice.localservice_api.entity.Item;
import com.localservice.localservice_api.repository.ItemRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ItemService {

	private final ItemRepository itemRepository;

	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	// Create a new item
	public Item createItem(Item item) {
		return itemRepository.save(item);
	}

	// Retrieve all items
	public List<Item> getAllItems() {
		return itemRepository.findAll();
	}

	// Retrieve an item by ID
	public Optional<Item> getItemById(long itemId) {
		return itemRepository.findById(itemId);
	}

	// Update an existing item
	public Item updateItem(long itemId, Item updatedItem) {
		return getItemById(itemId).map(item -> {
			item.setItem_name(updatedItem.getItem_name());
			item.setType(updatedItem.getType());
			item.setUnit_price(updatedItem.getUnit_price());
			item.setStock_qty(updatedItem.getStock_qty());
			return itemRepository.save(item);
		}).orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
	}

	// Delete an item by ID
	public void deleteItem(long itemId) {
		getItemById(itemId)
				.orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
		itemRepository.deleteById(itemId);

	}

	public MultipleUpdateResponseDto<Item> updateMultipleItems(List<Item> items) {
		MultipleUpdateResponseDto<Item> res = new MultipleUpdateResponseDto<Item>();
		items.stream().map(item -> {
            try {
                Item existingItem = itemRepository.findById(item.getItem_id())
                    .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + item.getItem_id()));
                existingItem.setItem_name(item.getItem_name());
                existingItem.setType(item.getType());
                existingItem.setUnit_price(item.getUnit_price());
                existingItem.setStock_qty(item.getStock_qty());
                updateItem(existingItem.getItem_id(), existingItem);
                res.getUpdated().add(item);
                return item.getItem_id();
            } catch (Exception e) {
                res.getNotUpdated().add(item);
                return item.getItem_id();
            }
        }).collect(Collectors.toList());
		return res;
	}

}
