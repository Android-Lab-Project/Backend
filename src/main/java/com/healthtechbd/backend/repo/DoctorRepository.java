package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByAppUser_Id(Long appUser_id);

    void deleteByAppUser_Id(Long id);

    boolean existsByAppUser_Email(String email);

    Doctor findByAppUser_Email(String email);
}
