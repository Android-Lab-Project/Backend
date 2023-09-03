package com.healthtechbd.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.healthtechbd.backend.entity.Admin;
public interface AdminRepository extends JpaRepository<Admin,Long> {
}
