package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class rpDTO {

   private List<DiagnosisReportDTO> allReports;

   private List<DoctorPrescriptionDTO> allPrescriptions;
}
