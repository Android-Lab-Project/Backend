package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AddDiagnosisDTO;
import com.healthtechbd.backend.dto.DiagnosisDTO;
import com.healthtechbd.backend.dto.DiagnosisOrderDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Diagnosis;
import com.healthtechbd.backend.entity.DiagnosisOrder;
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

import java.time.LocalDate;
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

        if (addDiagnosisDTO.getCost() == null || addDiagnosisDTO.getCost() <= 0) {
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

    @PostMapping("/diagnosis/order")
    public ResponseEntity<?> createDiagnosisOrder(@RequestBody DiagnosisOrderDTO diagnosisOrderDTO, HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        Optional<AppUser> opHospital = userRepository.findById(diagnosisOrderDTO.getHospitalId());

        AppUser hospital = new AppUser();

        if (!opHospital.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital does not exist"), HttpStatus.BAD_REQUEST);
        }

        hospital = opHospital.get();

        DiagnosisOrder diagnosisOrder = modelMapper.map(diagnosisOrderDTO, DiagnosisOrder.class);

        diagnosisOrder.setUser(appUser);
        diagnosisOrder.setHospital(hospital);
        diagnosisOrder.setDate(LocalDate.now());
        diagnosisOrderRepository.save(diagnosisOrder);

        return new ResponseEntity<>(ApiResponse.create("create", "Diagnosis order created"), HttpStatus.OK);

    }


}
