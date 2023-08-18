package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbulanceRepository extends JpaRepository<Ambulance,Long> {
}
