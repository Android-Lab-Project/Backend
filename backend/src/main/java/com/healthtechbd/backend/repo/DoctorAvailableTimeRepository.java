package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.DoctorAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorAvailableTimeRepository extends JpaRepository<DoctorAvailableTime,Long> {
}
