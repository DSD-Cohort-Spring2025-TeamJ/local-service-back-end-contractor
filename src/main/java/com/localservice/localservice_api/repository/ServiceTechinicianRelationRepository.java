package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.ServiceTechnicianRelation;
import com.localservice.localservice_api.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceTechinicianRelationRepository extends JpaRepository<ServiceTechnicianRelation, Long> {

    @Query("SELECT t FROM Technician t WHERE t.tech_id IN (SELECT str.technician.tech_id FROM ServiceTechnicianRelation str WHERE str.service.service_id = :service_id)")
    List<Technician> getTechiciansByService_id(@Param("service_id") Long serviceId);

}
