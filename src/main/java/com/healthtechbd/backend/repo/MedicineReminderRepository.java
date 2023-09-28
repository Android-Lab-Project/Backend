package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.MedicineReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MedicineReminderRepository extends JpaRepository<MedicineReminder, Long> {
    List<MedicineReminder> findByAppUser_Id(Long appUser_id);
}
