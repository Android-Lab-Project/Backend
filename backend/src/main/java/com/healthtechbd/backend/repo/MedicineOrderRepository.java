package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.MedicineOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineOrderRepository extends JpaRepository<MedicineOrder,Long> {
}
