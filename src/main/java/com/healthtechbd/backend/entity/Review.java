package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long starCount;

    private String review;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private AppUser reviewer;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private AppUser subject;
}
