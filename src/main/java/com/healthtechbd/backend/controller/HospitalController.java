package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DiagnosisDTO;
import com.healthtechbd.backend.dto.DiagnosisOrderViewDTO;
import com.healthtechbd.backend.dto.HospitalDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Diagnosis;
import com.healthtechbd.backend.entity.DiagnosisOrder;
import com.healthtechbd.backend.entity.Hospital;
import com.healthtechbd.backend.repo.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
@PreAuthorize("hasAuthority('HOSPITAL')")
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
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("permitAll()")
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
        var roles = appUser.getRoles();
        String password = appUser.getPassword();

        SignUpDTO signUpDTO = modelMapper.map(hospital.getAppUser(), SignUpDTO.class);
        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        if (updateUserResponse.getResponse().haveError()) {
            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.BAD_REQUEST);
        }

        appUser = updateUserResponse.getUser();

        if (appUser.getPassword() == null) {
            appUser.setPassword(password);
        }

        appUser.setId(appUserId);
        appUser.setRoles(roles);

        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(appUserId);

        if (optionalHospital.isPresent()) {
            Hospital existingHospital = optionalHospital.get();
            hospital.setId(existingHospital.getId());
            hospital.setBalance(existingHospital.getBalance());
            hospital.setAppUser(appUser);

            hospitalRepository.save(hospital);

            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('HOSPITAL','USER')")
    @GetMapping("/hospital/{id}")
    public ResponseEntity<?> getHospitalInfo(@PathVariable(name = "id") Long id) {
        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(id);

        if (optionalHospital.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.NOT_FOUND);
        }

        Hospital hospital = optionalHospital.get();

        HospitalDTO hospitalDTO = modelMapper.map(hospital, HospitalDTO.class);
        hospitalDTO.setId(id);
        hospitalDTO.setFirstName(hospital.getAppUser().getFirstName());
        hospitalDTO.setLastName(hospital.getAppUser().getLastName());
        hospitalDTO.setEmail(hospital.getAppUser().getEmail());
        hospitalDTO.setContactNo(hospital.getAppUser().getContactNo());
        hospitalDTO.setDp(hospital.getAppUser().getDp());
        hospitalDTO.setRating(reviewRepository.findAvgRating(hospital.getAppUser().getId()));
        hospitalDTO.setReviewCount(reviewRepository.findCount(hospital.getAppUser().getId()));

        if (hospitalDTO.getRating() == null) {
            hospitalDTO.setRating(0.0);
        }

        if (hospitalDTO.getReviewCount() == null) {
            hospitalDTO.setReviewCount(0L);
        }

        return new ResponseEntity<>(hospitalDTO, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyAuthority('HOSPITAL','USER')")
    @GetMapping("/hospital/{id}/diagnoses")
    public ResponseEntity<?> getAlldiagnoses(@PathVariable(name = "id") Long id) {
        Optional<AppUser> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();

            Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(user.getId());

            if (optionalHospital.isPresent()) {
                Hospital hospital = optionalHospital.get();
                List<Diagnosis> diagnoses = diagnosisRepository.findByHospital_Id(hospital.getId());

                if (diagnoses.isEmpty()) {
                    return new ResponseEntity<>(ApiResponse.create("empty", "No Diagnosis Found"), HttpStatus.OK);
                }

                List<DiagnosisDTO> diagnosisDTOS = new ArrayList<>();

                for (Diagnosis diagnosis : diagnoses) {
                    DiagnosisDTO dto = modelMapper.map(diagnosis, DiagnosisDTO.class);
                    dto.setHospitalId(hospital.getAppUser().getId());
                    dto.setHospitalName(hospital.getHospitalName());
                    dto.setPlace(hospital.getPlace());
                    dto.setRating(reviewRepository.findAvgRating(hospital.getAppUser().getId()));
                    diagnosisDTOS.add(dto);
                }

                return new ResponseEntity<>(diagnosisDTOS, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "User not found"), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/update/diagnosis/order/report/{id}")
    public ResponseEntity<?> updateDiagnosisReport(@RequestParam(name = "report") String report,
                                                   @PathVariable(name = "id") Long id) {
        Optional<DiagnosisOrder> optionalDiagnosisOrder = diagnosisOrderRepository.findById(id);

        if (optionalDiagnosisOrder.isPresent()) {
            DiagnosisOrder diagnosisOrder = optionalDiagnosisOrder.get();
            diagnosisOrder.setReportURL(report);
            diagnosisOrderRepository.save(diagnosisOrder);

            return new ResponseEntity<>(ApiResponse.create("update", "Report added"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Diagnosis order not found"), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/dashboard/hospital/pending")
    public ResponseEntity<?> getAllPendingReports(HttpServletRequest request) {
        AppUser hospital = userService.returnUser(request);

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository
                .findByReportURLIsNullAndHospitalId(hospital.getId());

        if (diagnosisOrders.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending found"), HttpStatus.OK);
        }

        List<DiagnosisOrderViewDTO> diagnosisOrderViewDTOS = new ArrayList<>();

        for (var i : diagnosisOrders) {
            DiagnosisOrderViewDTO diagnosisOrderViewDTO = new DiagnosisOrderViewDTO();
            diagnosisOrderViewDTO.setId(i.getId());
            diagnosisOrderViewDTO.setDescription(i.getDescription());
            diagnosisOrderViewDTO.setTime(i.getTime());
            diagnosisOrderViewDTO.setDate(i.getOrderDate());
            diagnosisOrderViewDTO.setPlace(i.getPlace());
            diagnosisOrderViewDTO.setPatientName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
            diagnosisOrderViewDTO.setPatientId(i.getUser().getId());
            diagnosisOrderViewDTO.setContactNo(i.getUser().getContactNo());

            diagnosisOrderViewDTOS.add(diagnosisOrderViewDTO);
        }

        return new ResponseEntity<>(diagnosisOrderViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/dashboard/hospital/upcoming")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request) {
        AppUser hospital = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now());

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository.findByDateAndTimeAndHospitalId(LocalDate.now(),
                time, hospital.getId());

        if (diagnosisOrders.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<DiagnosisOrderViewDTO> diagnosisOrderViewDTOS = new ArrayList<>();

        for (var i : diagnosisOrders) {
            DiagnosisOrderViewDTO diagnosisOrderViewDTO = new DiagnosisOrderViewDTO();
            diagnosisOrderViewDTO.setId(i.getId());
            diagnosisOrderViewDTO.setDescription(i.getDescription());
            diagnosisOrderViewDTO.setTime(i.getTime());
            diagnosisOrderViewDTO.setDate(i.getOrderDate());
            diagnosisOrderViewDTO.setPlace(i.getPlace());
            diagnosisOrderViewDTO.setPatientName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
            diagnosisOrderViewDTO.setPatientId(i.getUser().getId());
            diagnosisOrderViewDTO.setContactNo(i.getUser().getContactNo());

            diagnosisOrderViewDTOS.add(diagnosisOrderViewDTO);
        }

        return new ResponseEntity<>(diagnosisOrderViewDTOS, HttpStatus.OK);
    }

}
