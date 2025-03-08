package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
}
