package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.AppUserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserNotificationRepository extends JpaRepository<AppUserNotification,Long> {
    List<AppUserNotification> findByCheckedFalse();

    List<AppUserNotification> findByCheckedFalseAndUserId(Long userId);
}
