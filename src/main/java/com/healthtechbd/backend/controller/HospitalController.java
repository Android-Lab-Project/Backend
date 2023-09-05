package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DiagnosisDTO;
import com.healthtechbd.backend.dto.DiagnosisViewDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Diagnosis;
import com.healthtechbd.backend.entity.DiagnosisOrder;
import com.healthtechbd.backend.entity.Hospital;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DiagnosisOrderRepository;
import com.healthtechbd.backend.repo.DiagnosisRepository;
import com.healthtechbd.backend.repo.HospitalRepository;
import com.healthtechbd.backend.service.TimeService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import com.healthtechbd.backend.utils.UpdateUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class HospitalController {


    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/register/hospital")
    public ResponseEntity<?> registerHospital(@RequestBody Hospital hospital) {
        SignUpDTO signUpDTO = modelMapper.map(hospital.getAppUser(), SignUpDTO.class);
        RegistrationResponse response = userService.registerUser(signUpDTO, "HOSPITAL");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }
        if (hospital.getHospitalName() == null || hospital.getHospitalName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital name is empty"), HttpStatus.BAD_REQUEST);
        }
        hospital.setAppUser(response.getUser());

        hospital.setBalance(0L);

        hospitalRepository.save(hospital);

        userService.AddUserCount(LocalDate.now());

        return new ResponseEntity<>(ApiResponse.create("create", "Sign up successful"), HttpStatus.OK);
    }

    @PostMapping("/update/hospital")
    public ResponseEntity<?> updateHospital(HttpServletRequest request, @RequestBody Hospital hospital) {
        AppUser appUser = userService.returnUser(request);
        Long appUserId = appUser.getId();

        SignUpDTO signUpDTO = modelMapper.map(hospital.getAppUser(), SignUpDTO.class);

        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        if (updateUserResponse.getResponse().haveError()) {
            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.BAD_REQUEST);
        }

        appUser = updateUserResponse.getUser();
        appUser.setId(appUserId);

        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(appUserId);

        hospital.setId(optionalHospital.get().getId());
        hospital.setAppUser(appUser);

        hospitalRepository.save(hospital);

        return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
    }


    @GetMapping("/hospital/{id}/diagnoses")
    public ResponseEntity<?> getAlldiagnoses(@PathVariable(name="id")Long id) {
        Optional<AppUser>optional =userRepository.findById(id);
        AppUser user = optional.get();
        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(user.getId());
        List<Diagnosis> diagnoses = diagnosisRepository.findByHospital_Id(optionalHospital.get().getId());

        if (diagnoses.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Diagnosis Found"), HttpStatus.OK);
        }

        List<DiagnosisDTO> diagnosisDTOS = new ArrayList<>();

        for (int i = 0; i < diagnoses.size(); i++) {
            diagnosisDTOS.add(modelMapper.map(diagnoses.get(i), DiagnosisDTO.class));
            diagnosisDTOS.get(i).setAppUser_id(diagnoses.get(i).getHospital().getAppUser().getId());
            diagnosisDTOS.get(i).setHospitalName(diagnoses.get(i).getHospital().getHospitalName());
            diagnosisDTOS.get(i).setPlace(diagnoses.get(i).getHospital().getPlace());
        }

        return new ResponseEntity<>(diagnosisDTOS, HttpStatus.OK);
    }

    @GetMapping("/dashboard/hospital/pending")
    public ResponseEntity<?> getAllPendingReports(HttpServletRequest request) {
        AppUser hospital = userService.returnUser(request);

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository.findByReportURLIsNullAndHospitalId(hospital.getId());

        if (diagnosisOrders.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending found"), HttpStatus.OK);
        }

        List<DiagnosisViewDTO> diagnosisViewDTOS = new ArrayList<>();

        for (var i : diagnosisOrders) {
            DiagnosisViewDTO diagnosisViewDTO = new DiagnosisViewDTO();
            diagnosisViewDTO.setId(i.getId());
            diagnosisViewDTO.setDescription(i.getDescription());
            diagnosisViewDTO.setTime(i.getTime());
            diagnosisViewDTO.setPlace(i.getPlace());
            diagnosisViewDTO.setPatientName(i.getUser().getFirstName() + " " + i.getUser().getLastName());

            diagnosisViewDTOS.add(diagnosisViewDTO);
        }

        return new ResponseEntity<>(diagnosisViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/dashboard/hospital/upcoming")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request) {
        AppUser hospital = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now());

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository.findByDateAndTimeAndHospitalId(LocalDate.now(), time, hospital.getId());

        if (diagnosisOrders.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<DiagnosisViewDTO> diagnosisViewDTOS = new ArrayList<>();

        for (var i : diagnosisOrders) {
            DiagnosisViewDTO diagnosisViewDTO = new DiagnosisViewDTO();
            diagnosisViewDTO.setId(i.getId());
            diagnosisViewDTO.setDescription(i.getDescription());
            diagnosisViewDTO.setTime(i.getTime());
            diagnosisViewDTO.setPlace(i.getPlace());
            diagnosisViewDTO.setPatientName(i.getUser().getFirstName() + " " + i.getUser().getLastName());

            diagnosisViewDTOS.add(diagnosisViewDTO);
        }

        return new ResponseEntity<>(diagnosisViewDTOS, HttpStatus.OK);


    }



}
