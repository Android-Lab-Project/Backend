package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    List<Diagnosis> findByHospital_Id(Long id);
}
