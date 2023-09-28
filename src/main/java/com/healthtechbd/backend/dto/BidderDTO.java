package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidderDTO {
    private Long id;
    private String name;
    private String email;
    private Double rating;
    private String contactNo;
}
