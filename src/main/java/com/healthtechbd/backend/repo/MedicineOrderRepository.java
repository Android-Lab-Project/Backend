package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.MedicineOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, Long> {
    @Query("SELECT COUNT(mo.id) " +
            "FROM MedicineOrder mo " +
            "WHERE mo.pharmacy.id = :pharmacyId " +
            "AND mo.date BETWEEN :startDate AND :endDate")
    Long countSerialsByPharmacyAndDate(
            @Param("pharmacyId") Long pharmacyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(mo.id) " +
            "FROM MedicineOrder mo " +
            "WHERE mo.pharmacy.id = :pharmacyId")
    Long countSerialsByPharmacy(
            @Param("pharmacyId") Long pharmacyId);

    @Query("SELECT mo.date, SUM(mo.price) " +
            "FROM MedicineOrder mo " +
            "WHERE mo.pharmacy.id = :pharmacyId " +
            "AND mo.date BETWEEN :startDate AND :endDate " +
            "GROUP BY mo.date")
    List<Object[]> sumPriceByPharmacyAndDateGroupByDate(
            @Param("pharmacyId") Long pharmacyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(mo.price) " +
            "FROM MedicineOrder mo " +
            "WHERE mo.pharmacy.id = :pharmacyId " +
            "AND mo.date BETWEEN :startDate AND :endDate")
    Long sumPriceByPharmacyAndDate(@Param("pharmacyId") Long pharmacyId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(mo.price) " +
            "FROM MedicineOrder mo " +
            "WHERE mo.pharmacy.id = :pharmacyId")
    Long sumPriceByPharmacy(@Param("pharmacyId") Long pharmacyId);

    @Query("SELECT mo " +
            "FROM MedicineOrder mo " +
            "WHERE mo.user.id = :userId " +
            "AND mo.delivered = 0")
    List<MedicineOrder> findUndeliveredOrdersByUser(@Param("userId") Long userId);

    @Query("SELECT mo " +
            "FROM MedicineOrder mo " +
            "WHERE mo.pharmacy.id = :pharmacyId " +
            "AND mo.delivered = 0")
    List<MedicineOrder> findUndeliveredOrdersByPharmacy(@Param("pharmacyId") Long pharmacyId);


}