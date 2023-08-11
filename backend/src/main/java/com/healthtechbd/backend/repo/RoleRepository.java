package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleType(String roleType);
}
