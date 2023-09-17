package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmbulanceTripPendingViewDTO {
    private Long id;
    List<BidderDTO> bidders;
    private String source;
    private String destination;
    private Long price;
    private LocalDate orderDate;
}
