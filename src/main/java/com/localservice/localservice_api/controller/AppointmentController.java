package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.dto.AppointmentRequestDto;
import com.localservice.localservice_api.entity.Appointment;
import com.localservice.localservice_api.response.AdminAppointmentViewDTO;
import com.localservice.localservice_api.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PostMapping
	public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequestDto appointmentRequestDto) {
		try {
			Appointment savedAppointment = appointmentService.createAppointment(appointmentRequestDto);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating appointment: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> viewAllAppointments() {
		try {
			List<Appointment> appointments = appointmentService.viewAllAppointments();
			return ResponseEntity.ok(appointments);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving appointments: " + e.getMessage());
		}
	}

	@GetMapping("/{appointment_id}")
	public ResponseEntity<?> viewSingleAppointment(@PathVariable Long appointment_id) {
		try {
			Optional<Appointment> appointment = appointmentService.viewSingleAppointment(appointment_id);
			if (appointment.isPresent()) {
				return ResponseEntity.ok(appointment);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Appointment not found for ID: " + appointment_id);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving appointment: " + e.getMessage());
		}
	}

	@GetMapping("/admin/{appointment_id}")
	public ResponseEntity<?> viewAllAppointmentsAdmin(@PathVariable Long appointment_id) {
		try {
			AdminAppointmentViewDTO appointment = appointmentService.viewAdminViewAppointment(appointment_id);
			return ResponseEntity.ok(appointment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving admin appointment view: " + e.getMessage());
		}
	}

	@PutMapping("/admin/{appointment_id}")
	public ResponseEntity<?> updateItemInventoryForAppointment(@PathVariable Long appointment_id) {
		try {
			String message = appointmentService.updateItemInventoryAndNotify(appointment_id);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving admin appointment view: " + e.getMessage());
		}
	}

	@PutMapping("/admin/{appointment_id}/{appointment_status}")
	public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long appointment_id,
			@PathVariable String appointment_status) {

		try {
			Appointment appointment = appointmentService.updateAppointmentStatus(appointment_id, appointment_status);
			return ResponseEntity.ok(appointment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating appointment: " + e.getMessage());
		}
	}

	@PutMapping("/admin/{appointment_id}/updateTime")
	public ResponseEntity<?> updateAppointmentTime(@PathVariable Long appointment_id, @RequestParam String dateTime,
			@RequestParam String endDateTime) {
		try {
			Appointment appointment = appointmentService.updateAppointmentTimes(appointment_id, dateTime, endDateTime);
			return ResponseEntity.ok(appointment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating appointment: " + e.getMessage());
		}
	}

	@GetMapping("/admin/filter")
	public ResponseEntity<?> viewAllAppointmentsByStatus(@RequestParam String status) {
		List<Appointment> appointments = appointmentService.viewAllAppointmentsBysStatus(status);
		return ResponseEntity.ok(appointments);
	}
}
