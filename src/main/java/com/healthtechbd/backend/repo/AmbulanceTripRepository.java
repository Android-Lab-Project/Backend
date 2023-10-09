package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.AmbulanceTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AmbulanceTripRepository extends JpaRepository<AmbulanceTrip, Long> {

    @Query("SELECT COUNT(at.id) " +
            "FROM AmbulanceTrip at " +
            "WHERE at.ambulanceProvider.id = :ambulanceProviderId " +
            "AND at.date BETWEEN :startDate AND :endDate")
    Long countTripsByAmbulanceProviderAndDate(
            @Param("ambulanceProviderId") Long ambulanceProviderId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(at.id) " +
            "FROM AmbulanceTrip at " +
            "WHERE at.ambulanceProvider.id = :ambulanceProviderId")
    Long countTripsByAmbulanceProvider(@Param("ambulanceProviderId") Long ambulanceProviderId);

    @Query("SELECT COUNT(at.id) " +
            "FROM AmbulanceTrip at ")
    Long countAmbulanceTrips();

    @Query("SELECT COUNT(at.id) " +
            "FROM AmbulanceTrip at " + "WHERE at.date BETWEEN :startDate AND :endDate")
    Long countAmbulanceTripsByDate(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT at.date, COUNT(at.id) " +
            "FROM DiagnosisOrder at " +
            "WHERE at.date BETWEEN :startDate AND :endDate " +
            "GROUP BY at.date " +
            "ORDER BY at.date DESC")
    List<Object[]> countAmbulanceTripsGroupByDate(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT at.date, SUM(at.price) " +
            "FROM AmbulanceTrip at " +
            "WHERE at.ambulanceProvider.id = :ambulanceProviderId " +
            "AND at.date BETWEEN :startDate AND :endDate " +
            "GROUP BY at.date")
    List<Object[]> sumPriceByAmbulanceProviderAndDateGroupByDate(
            @Param("ambulanceProviderId") Long ambulanceProviderId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(at.price) " +
            "FROM AmbulanceTrip at " +
            "WHERE at.ambulanceProvider.id = :ambulanceProviderId " +
            "AND at.date BETWEEN :startDate AND :endDate")
    Long sumPriceByAmbulanceProviderAndDate(
            @Param("ambulanceProviderId") Long ambulanceProviderId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(at.price) " +
            "FROM AmbulanceTrip at " +
            "WHERE at.ambulanceProvider.id = :ambulanceProviderId")
    Long sumPriceByAmbulanceProvider(@Param("ambulanceProviderId") Long ambulanceProviderId);

    @Query("SELECT at FROM AmbulanceTrip at WHERE at.orderDate>=:date AND at.user.id = :userId AND at.ambulanceProvider.id is Not NULL AND at.checked = 0 ")
    List<AmbulanceTrip> findUpcomingTripsByUser(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Query("SELECT at FROM AmbulanceTrip at WHERE at.orderDate>=:date AND at.ambulanceProvider.id = :ambulanceProviderId  AND at.checked = 0")
    List<AmbulanceTrip> findUpcomingTripsByProvider(@Param("date") LocalDate date, @Param("ambulanceProviderId") Long ambulanceProviderId);

    @Query("SELECT at FROM AmbulanceTrip at WHERE at.ambulanceProvider IS NULL AND NOT EXISTS (SELECT 1 FROM at.bidders b WHERE b.id = :id)")
    List<AmbulanceTrip> findByAmbulanceProviderIsNullAndNotBidder(@Param("id") Long id);

    @Query("SELECT at FROM AmbulanceTrip at WHERE  at.user.id = :userId AND at.ambulanceProvider.id is NULL")
    List<AmbulanceTrip> findTripsByUserProviderNULL(@Param("userId") Long userId);


    @Query("SELECT at FROM AmbulanceTrip at WHERE at.user.id = :userId AND at.reviewChecked = 0")
    List<AmbulanceTrip> findTripByReviewChecked(@Param("userId") Long userId);


    @Modifying
    @Query("UPDATE AmbulanceTrip SET reviewChecked = 1 WHERE id = :o_id AND user.id = :user_id AND ambulanceProvider.id = :subject_id")
    void updateReviewChecked(@Param("o_id") Long o_id, @Param("user_id") Long user_id, @Param("subject_id") Long subject_id);

}
