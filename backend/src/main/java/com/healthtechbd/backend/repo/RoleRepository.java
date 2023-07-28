package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Role;
import com.healthtechbd.backend.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleType(RoleType roleType);
}
