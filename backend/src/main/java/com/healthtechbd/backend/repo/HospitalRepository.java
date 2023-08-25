package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByAppUser_Id(Long id);
}
