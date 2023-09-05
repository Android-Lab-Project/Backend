package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {

    @Query("SELECT ur FROM UserResponse ur WHERE ur.checked = 0")
    List<UserResponse> findByChecked();
}
