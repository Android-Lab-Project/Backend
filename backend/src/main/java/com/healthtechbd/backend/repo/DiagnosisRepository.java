package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long> {
}
