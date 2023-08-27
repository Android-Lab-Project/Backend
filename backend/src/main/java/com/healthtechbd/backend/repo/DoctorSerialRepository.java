package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.DoctorSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DoctorSerialRepository extends JpaRepository<DoctorSerial, Long> {
    @Query("SELECT COUNT(ds.id) " +
            "FROM DoctorSerial ds " +
            "WHERE ds.doctor.id = :doctorId " +
            "AND ds.date BETWEEN :startDate AND :endDate")
    Long countSerialsByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ds.id) " +
            "FROM DoctorSerial ds " +
            "WHERE ds.doctor.id = :doctorId")
    Long countSerialsByDoctor(
            @Param("doctorId") Long doctorId);

    @Query("SELECT ds.date, SUM(ds.price) " +
            "FROM DoctorSerial ds " +
            "WHERE ds.doctor.id = :doctorId " +
            "AND ds.date BETWEEN :startDate AND :endDate " +
            "GROUP BY ds.date")
    List<Object[]> sumPriceByDoctorAndDateGroupByDate(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(ds.price) " +
            "FROM DoctorSerial ds " +
            "WHERE ds.doctor.id = :doctorId " +
            "AND ds.date BETWEEN :startDate AND :endDate")
    Long sumPriceByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(ds.price) " +
            "FROM DoctorSerial ds " +
            "WHERE ds.doctor.id = :doctorId")
    Long sumPriceByDoctor(
            @Param("doctorId") Long doctorId);

    List<DoctorSerial> findByUser_Id(Long user_id);
}
