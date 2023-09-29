package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AddDiagnosisDTO;
import com.healthtechbd.backend.dto.DiagnosisDTO;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
public class DiagnosisController {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('HOSPITAL')")
    @PostMapping("/add/diagnosis")
    public ResponseEntity<?> addDiagnosis(@RequestBody AddDiagnosisDTO addDiagnosisDTO, HttpServletRequest request) {
        if (addDiagnosisDTO.getName() == null || addDiagnosisDTO.getName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Name can't be empty"), HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getDescription() == null || addDiagnosisDTO.getDescription().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Description can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getPrice() == null || addDiagnosisDTO.getPrice() <= 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Invalid Cost"), HttpStatus.BAD_REQUEST);
        }
        if (addDiagnosisDTO.getStartTime() == null || addDiagnosisDTO.getStartTime().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "StartTime can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }
        if (addDiagnosisDTO.getEndTime() == null || addDiagnosisDTO.getEndTime().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "EndTime can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }
        if (addDiagnosisDTO.getDeptContactNo() == null || addDiagnosisDTO.getDeptContactNo().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Contact can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }


        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.OK);
        }

        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(appUser.getId());

        if (optionalHospital.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.NOT_FOUND);
        }

        Diagnosis diagnosis = new Diagnosis();

        diagnosis = modelMapper.map(addDiagnosisDTO, Diagnosis.class);

        diagnosis.setHospital(optionalHospital.get());

        diagnosisRepository.save(diagnosis);

        return new ResponseEntity<>(ApiResponse.create("create", "Diagnosis added"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('HOSPITAL')")
    @PostMapping("/update/diagnosis/{id}")
    public ResponseEntity<?> updateDiagnosis(@RequestBody AddDiagnosisDTO addDiagnosisDTO, @PathVariable(name = "id") Long id) {

        if (addDiagnosisDTO.getName() == null || addDiagnosisDTO.getName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Name can't be empty"), HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getDescription() == null || addDiagnosisDTO.getDescription().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Description can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getPrice() == null || addDiagnosisDTO.getPrice() <= 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Invalid Cost"), HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getStartTime() == null || addDiagnosisDTO.getStartTime().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "StartTime can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getEndTime() == null || addDiagnosisDTO.getEndTime().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "EndTime can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getDeptContactNo() == null || addDiagnosisDTO.getDeptContactNo().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Contact  can't be empty"),
                    HttpStatus.BAD_REQUEST);
        }


        Optional<Diagnosis> optionalDiagnosis = diagnosisRepository.findById(id);

        if (optionalDiagnosis.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Diagnosis not found"), HttpStatus.NOT_FOUND);
        }

        Diagnosis diagnosis = optionalDiagnosis.get();

        diagnosis.setDescription(addDiagnosisDTO.getDescription());
        diagnosis.setPrice(addDiagnosisDTO.getPrice());
        diagnosis.setName(addDiagnosisDTO.getName());
        diagnosis.setStartTime(addDiagnosisDTO.getStartTime());
        diagnosis.setEndTime(addDiagnosisDTO.getEndTime());
        diagnosis.setDeptContactNo(addDiagnosisDTO.getDeptContactNo());

        diagnosisRepository.save(diagnosis);

        return new ResponseEntity<>(ApiResponse.create("update", "Diagnosis updated"), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('HOSPITAL','USER')")
    @GetMapping("/diagnosis/all")
    public ResponseEntity<?> showAllDiagnosisDetails() {

        List<Diagnosis> diagnoses = diagnosisRepository.findAll();

        if (diagnoses.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Diagnosis Found"), HttpStatus.OK);
        }

        List<DiagnosisDTO> diagnosisDTOS = new ArrayList<>();

        for (int i = 0; i < diagnoses.size(); i++) {
            diagnosisDTOS.add(modelMapper.map(diagnoses.get(i), DiagnosisDTO.class));
            diagnosisDTOS.get(i).setHospitalId(diagnoses.get(i).getHospital().getAppUser().getId());
            diagnosisDTOS.get(i).setHospitalName(diagnoses.get(i).getHospital().getHospitalName());
            diagnosisDTOS.get(i).setPlace(diagnoses.get(i).getHospital().getPlace());
            diagnosisDTOS.get(i)
                    .setRating(reviewRepository.findAvgRating(diagnoses.get(i).getHospital().getAppUser().getId()));
        }

        return new ResponseEntity<>(diagnosisDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USER','HOSPITAL')")
    @GetMapping("/diagnosis/order/check/{id}")
    public  ResponseEntity<?>makeChecked(@PathVariable(name="id")Long id)
    {
        Optional<DiagnosisOrder>optionalDiagnosisOrder = diagnosisOrderRepository.findById(id);

        if(optionalDiagnosisOrder.isEmpty())
        {
            return  new ResponseEntity<>(ApiResponse.create("error", "Not found"),HttpStatus.NOT_FOUND);
        }

        DiagnosisOrder diagnosisOrder = optionalDiagnosisOrder.get();
        diagnosisOrder.setChecked(1);
        diagnosisOrderRepository.save(diagnosisOrder);

        return new ResponseEntity<>(ApiResponse.create("checked","Diagnosis Order is now checked"),HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('HOSPITAL')")
    @DeleteMapping("/delete/diagnosis/{id}")
    public ResponseEntity<?> deleteDiagnosis(@PathVariable(name = "id") Long id) {
        try {
            diagnosisRepository.deleteById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.create("error", "Diagnosis can't be deleted"),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(ApiResponse.create("delete", "Diagnosis deleted"), HttpStatus.OK);
    }

}
