package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List<Appointment> findByEmail(String email);
}
