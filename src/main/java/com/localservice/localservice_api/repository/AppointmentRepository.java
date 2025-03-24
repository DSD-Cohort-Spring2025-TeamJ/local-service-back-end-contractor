package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.synced_with_calendar = false")
    List<Appointment> findAllUnsyncedAppointments();

}
