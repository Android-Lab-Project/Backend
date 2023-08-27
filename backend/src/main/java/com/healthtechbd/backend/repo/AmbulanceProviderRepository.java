package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.AmbulanceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmbulanceProviderRepository extends JpaRepository<AmbulanceProvider, Long> {
    Optional<AmbulanceProvider> findByAppUser_Id(Long id);
}
