package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.UserCountStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserCountStatsRepository  extends JpaRepository<UserCountStats,Long> {
    Optional<UserCountStats> findByDate(LocalDate date);

    @Query("SELECT SUM(st.count) FROM UserCountStats st")
    Long countUser();

    @Query("SELECT SUM(st.count) FROM UserCountStats st WHERE st.date BETWEEN :startDate AND :endDate")
    Long countUserByDate(@Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate);
}
