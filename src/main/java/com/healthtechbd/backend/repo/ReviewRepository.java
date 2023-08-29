package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySubject_Id(Long id);
}
