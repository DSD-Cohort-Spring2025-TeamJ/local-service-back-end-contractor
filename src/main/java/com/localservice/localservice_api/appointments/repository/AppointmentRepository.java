package com.localservice.localservice_api.appointments.repository;

import com.localservice.localservice_api.appointments.model.AppointmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {

}
