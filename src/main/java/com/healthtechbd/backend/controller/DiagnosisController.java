package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AddDiagnosisDTO;
import com.healthtechbd.backend.dto.DiagnosisDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Diagnosis;
import com.healthtechbd.backend.entity.Hospital;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DiagnosisOrderRepository;
import com.healthtechbd.backend.repo.DiagnosisRepository;
import com.healthtechbd.backend.repo.HospitalRepository;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
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
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;


    @PostMapping("/add/diagnosis")
    public ResponseEntity<?> addDiagnosis(@RequestBody AddDiagnosisDTO addDiagnosisDTO, HttpServletRequest request) {
        if (addDiagnosisDTO.getName() == null || addDiagnosisDTO.getName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Name can't be empty"), HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getDescription() == null || addDiagnosisDTO.getDescription().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Description can't be empty"), HttpStatus.BAD_REQUEST);
        }

        if (addDiagnosisDTO.getPrice() == null || addDiagnosisDTO.getPrice() <= 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Invalid Cost"), HttpStatus.BAD_REQUEST);
        }

        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.OK);
        }

        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(appUser.getId());

        if (!optionalHospital.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
        }

        Diagnosis diagnosis = new Diagnosis();

        diagnosis = modelMapper.map(addDiagnosisDTO, Diagnosis.class);

        diagnosis.setHospital(optionalHospital.get());

        diagnosisRepository.save(diagnosis);

        return new ResponseEntity<>(ApiResponse.create("create", "Diagnosis added"), HttpStatus.OK);
    }


    @GetMapping("/diagnosis/all")
    public ResponseEntity<?> showAllDiagnosisDetails() {
        List<Diagnosis> diagnoses = diagnosisRepository.findAll();

        if (diagnoses.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Diagnosis Found"), HttpStatus.OK);
        }

        List<DiagnosisDTO> diagnosisDTOS = new ArrayList<>();

        for (int i = 0; i < diagnoses.size(); i++) {
            diagnosisDTOS.add(modelMapper.map(diagnoses.get(i), DiagnosisDTO.class));
            diagnosisDTOS.get(i).setAppUser_id(diagnoses.get(i).getHospital().getAppUser().getId());
            diagnosisDTOS.get(i).setHospitalName(diagnoses.get(i).getHospital().getHospitalName());
            diagnosisDTOS.get(i).setPlace(diagnoses.get(i).getHospital().getPlace());
            diagnosisDTOS.get(i).setRating(diagnoses.get(i).getHospital().getAppUser().getId());
        }

        return new ResponseEntity<>(diagnosisDTOS, HttpStatus.OK);
    }


    @DeleteMapping("/delete/diagnosis/{id}")
    public ResponseEntity<?>deleteDiagnosis(@PathVariable(name ="id")Long id)
    {
        try
        {
            diagnosisRepository.deleteById(id);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(ApiResponse.create("error", "Diagnosis can't be deleted"),HttpStatus.BAD_REQUEST);
        }

        return  new ResponseEntity<>(ApiResponse.create("delete","Diagnosis deleted"),HttpStatus.OK);
    }




}
