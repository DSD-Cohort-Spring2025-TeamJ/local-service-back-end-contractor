package com.localservice.localservice_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localservice.localservice_api.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

}
