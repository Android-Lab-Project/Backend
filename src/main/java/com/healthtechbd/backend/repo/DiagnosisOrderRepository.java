package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.DiagnosisOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiagnosisOrderRepository extends JpaRepository<DiagnosisOrder, Long> {

    @Query("SELECT COUNT(do.id) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId " +
            "AND do.date BETWEEN :startDate AND :endDate")
    Long countDiagnosisOrdersByHospitalAndDate(
            @Param("hospitalId") Long hospitalId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(do.id) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.hospital.id = :hospitalId")
    Long countDiagnosisOrdersByHospital(
            @Param("hospitalId") Long hospitalId);

    @Query("SELECT COUNT(do.id) " +
            "FROM DiagnosisOrder do ")
    Long countDiagnosisOrders();

    @Query("SELECT COUNT(do.id) " +
            "FROM DiagnosisOrder do " + "WHERE do.date BETWEEN :startDate AND :endDate")
    Long countDiagnosisOrdersByDate(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT do.date, COUNT(do.id) " +
            "FROM DiagnosisOrder do " +
            "WHERE do.date BETWEEN :startDate AND :endDate " +
            "GROUP BY do.date " +
            "ORDER BY do.date DESC")
    List<Object[]> countDiagnosisOrdersGroupByDate(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);


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
            "WHERE ((do.orderDate = :startDate AND do.time >= :time) OR do.orderDate > :startDate) " +
            "AND do.hospital.id = :hospitalId AND do.checked = 0")
    List<DiagnosisOrder> findByDateAndTimeAndHospitalId(
            @Param("startDate") LocalDate startDate,
            @Param("time") Double time,
            @Param("hospitalId") Long hospitalId);

    @Query("SELECT do FROM DiagnosisOrder do " +
            "WHERE ((do.orderDate = :startDate AND do.time >= :time) OR do.orderDate > :startDate) " +
            "AND do.user.id = :userId AND do.checked = 0 ")
    List<DiagnosisOrder> findByDateAndTimeAndUserId(
            @Param("startDate") LocalDate startDate,
            @Param("time") Double time,
            @Param("userId") Long userId);

    @Query("SELECT do FROM DiagnosisOrder do WHERE do.user.id = :userId AND do.reviewChecked = 0")
    List<DiagnosisOrder> findDiagnosisOrderByReviewChecked(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE DiagnosisOrder SET reviewChecked = 1 WHERE id = :o_id AND user.id = :user_id AND hospital.id = :subject_id")
    void updateReviewChecked(@Param("o_id") Long o_id, @Param("user_id") Long user_id, @Param("subject_id") Long subject_id);

}
