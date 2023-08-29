package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByAmbulanceProvider_Id(Long id);
}
