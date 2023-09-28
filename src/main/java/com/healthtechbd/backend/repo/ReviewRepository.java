package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySubject_Id(Long id);

    @Query("SELECT AVG(r.starCount) FROM Review r WHERE r.subject.id = :user_id")
    Double findAvgRating(@Param("user_id") Long user_id);

    @Query("SELECT AVG(r.starCount) FROM Review r WHERE r.subject.id = :user_id AND r.date BETWEEN :startDate AND :endDate")
    Double findAvgRatingByDate(@Param("user_id") Long user_id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT Count(r) FROM Review r WHERE r.subject.id = :user_id")
    Long findCount(@Param("user_id") Long user_id);

    @Query("Select Count(r) from Review r where r.reviewer.id = :reviewerId AND r.subject.id = :subjectId")
    Long countByUser(@Param("reviewerId") Long reviewerId, @Param("subjectId") Long subjectId);


}
