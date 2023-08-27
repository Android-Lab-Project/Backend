package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.AmbulanceTrip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbulanceTripRepository extends JpaRepository<AmbulanceTrip,Long> {
}
