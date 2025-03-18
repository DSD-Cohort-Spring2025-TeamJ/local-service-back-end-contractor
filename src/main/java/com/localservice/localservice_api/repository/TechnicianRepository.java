package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Object> findById(Optional<String> techId);
}
