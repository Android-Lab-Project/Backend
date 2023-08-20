package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    boolean existsByName(String name);

    boolean existsByCompany(String company);
}
