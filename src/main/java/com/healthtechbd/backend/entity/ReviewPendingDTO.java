package com.healthtechbd.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPendingDTO {
    private Long subjectId;
    private String subjectName;
    private Long orderId;
    private String role;
}
