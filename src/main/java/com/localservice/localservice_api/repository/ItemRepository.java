package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
