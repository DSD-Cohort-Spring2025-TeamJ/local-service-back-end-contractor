package com.localservice.localservice_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localservice.localservice_api.constants.Constants;
import com.localservice.localservice_api.entity.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	
	List<Appointment> findByStatus(Constants status);

}
