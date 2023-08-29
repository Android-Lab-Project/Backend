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

    List<DiagnosisOrder> findByUser_Id(Long user_id);

    List<DiagnosisOrder> findByUser_IdAndReportURLIsNotNull(Long user_id);

    List<DiagnosisOrder> findByReportURLIsNullAndHospitalId(Long hospitalId);

    @Query("SELECT do FROM DiagnosisOrder do " +
            "WHERE (do.date = :startDate AND do.time >= :time) " +
            "OR (do.date > :startDate) " +
            "AND do.hospital.id = :hospitalId")
    List<DiagnosisOrder> findByDateAndTimeAndHospitalId(
            @Param("startDate") LocalDate startDate,
            @Param("time") Double time,
            @Param("hospitalId") Long hospitalId);

    @Query("SELECT do FROM DiagnosisOrder do " +
            "WHERE (do.date = :startDate AND do.time >= :time) " +
            "OR (do.date > :startDate) " +
            "AND do.user.id = :userId")
    List<DiagnosisOrder> findByDateAndTimeAndUserId(
            @Param("startDate") LocalDate startDate,
            @Param("time") Double time,
            @Param("userId") Long userId);
}
