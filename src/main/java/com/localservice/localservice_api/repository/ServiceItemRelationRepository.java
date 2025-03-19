package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.Item;
import com.localservice.localservice_api.entity.ServiceItemRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemRelationRepository extends JpaRepository<ServiceItemRelation, Long> {

    @Query("SELECT sir.qty_needed FROM ServiceItemRelation sir WHERE sir.service.service_id = :service_id AND sir.item.item_id = :item_id")
    int getQtyNeededByItemid(@Param("item_id") Long itemId, @Param("service_id") Long serviceId);

    @Query("SELECT i FROM Item i WHERE i.item_id IN (SELECT sir.item.item_id FROM ServiceItemRelation sir WHERE sir.service.service_id = :service_id)")
    List<Item> getItemsByService_id(@Param("service_id") Long serviceId);

}
