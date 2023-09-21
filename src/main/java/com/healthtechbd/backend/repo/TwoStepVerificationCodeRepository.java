package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.TwoStepVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwoStepVerificationCodeRepository extends JpaRepository<TwoStepVerificationCode,Long> {
    Optional<TwoStepVerificationCode> findByEmail(String email);
}
