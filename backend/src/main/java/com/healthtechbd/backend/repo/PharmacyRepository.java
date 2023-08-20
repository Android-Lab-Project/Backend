package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Hospital;
import com.healthtechbd.backend.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacyRepository extends JpaRepository<Pharmacy,Long> {
    Optional<Pharmacy> findByAppUser_Id(Long id);
}
