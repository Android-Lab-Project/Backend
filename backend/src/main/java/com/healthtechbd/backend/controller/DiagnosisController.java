package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceDTO;
import com.healthtechbd.backend.dto.DiagnosisDTO;
import com.healthtechbd.backend.entity.Ambulance;
import com.healthtechbd.backend.entity.Diagnosis;
import com.healthtechbd.backend.repo.DiagnosisRepository;
import com.healthtechbd.backend.utils.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class DiagnosisController {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/diagnosis/all")
    public ResponseEntity<?>showAllDiagnosisDetails()
    {
        List<Diagnosis> diagnoses = diagnosisRepository.findAll();

        if (diagnoses.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "No Diagnosis Found"), HttpStatus.BAD_REQUEST);
        }

        List<DiagnosisDTO> diagnosisDTOS = new ArrayList<>();

        for (int i = 0; i < diagnoses.size(); i++) {
            diagnosisDTOS.add(modelMapper.map(diagnoses.get(i), DiagnosisDTO.class));
            diagnosisDTOS.get(i).setAppUser_id(diagnoses.get(i).getHospital().getAppUser().getId());
            diagnosisDTOS.get(i).setHospitalName(diagnoses.get(i).getHospital().getHospitalName());

        }

        return new ResponseEntity<>(diagnosisDTOS, HttpStatus.OK);
    }

}
