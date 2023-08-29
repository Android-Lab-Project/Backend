package com.healthtechbd.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class rpDTO {

    List<DiagnosisReportDTO> allReports;

    List<DoctorPrescriptionDTO> allPrescriptions;
}
