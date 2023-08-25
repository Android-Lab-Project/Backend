package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.DiagnosisOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiagnosisOrderRepository extends JpaRepository<DiagnosisOrder, Long> {

    @Query("SELECT COUNT(do.id) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId " +
            "AND do.date BETWEEN :startDate AND :endDate")
    Long countSerialsByHospitalAndDate(
            @Param("hospitalId") Long hospitalId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(do.id) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId")
    Long countSerialsByHospital(
            @Param("hospitalId") Long hospitalId);
    @Query("SELECT do.date, SUM(do.price) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId " +
            "AND do.date BETWEEN :startDate AND :endDate " +
            "GROUP BY do.date")
    List<Object[]> sumPriceByHospitalAndDateGroupByDate(
            @Param("hospitalId") Long hospitalId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(do.price) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId " +
            "AND do.date BETWEEN :startDate AND :endDate")
    Long sumPriceByHospitalAndDate(
            @Param("hospitalId") Long hospitalId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(do.price) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId")
    Long sumPriceByHospital(
            @Param("hospitalId") Long hospitalId);
}
