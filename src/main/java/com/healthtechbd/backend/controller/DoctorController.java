package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.*;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.BkashPaymentService;
import com.healthtechbd.backend.service.DoctorService;
import com.healthtechbd.backend.service.TimeService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.BkashRefundResponse;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DoctorAvailableTimeRepository doctorAvailableTimeRepository;

    @Autowired
    private DoctorOnlineAvailableTimeRepository doctorOnlineAvailableTimeRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private BkashPaymentService bkashPaymentService;

    @Autowired
    private TimeService timeService;

    @PreAuthorize("permitAll()")
    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorSignUpDTO doctorSignUpDTO) {

        SignUpDTO signUpDTO = modelMapper.map(doctorSignUpDTO.getAppUser(), SignUpDTO.class);

        RegistrationResponse response = userService.registerUser(signUpDTO, "DOCTOR");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }

        doctorSignUpDTO.setAppUser(response.getUser());

        Doctor doctor = modelMapper.map(doctorSignUpDTO, Doctor.class);

        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {
            doctor.getAvailableTimes().get(i).setId(null);
            doctor.getAvailableTimes().get(i).setCount(0);
            doctor.getAvailableTimes().get(i).setAvailTime(doctor.getAvailableTimes().get(i).getStartTime());
            doctor.getAvailableTimes().get(i)
                    .setDate(DoctorService.currentDate(doctor.getAvailableTimes().get(i).getDay()));

        }
        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            doctor.getAvailableOnlineTimes().get(i).setId(null);
            doctor.getAvailableOnlineTimes().get(i).setCount(0);
            doctor.getAvailableOnlineTimes().get(i).setAvailTime(doctor.getAvailableOnlineTimes().get(i).getStartTime());
            doctor.getAvailableOnlineTimes().get(i)
                    .setDate(DoctorService.currentDate(doctor.getAvailableOnlineTimes().get(i).getDay()));
        }

        doctor.setBalance(0L);

        doctorRepository.save(doctor);

        userService.AddUserCount(LocalDate.now());

        return new ResponseEntity<>(ApiResponse.create("create", "Doctor Sign up successful"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @PostMapping("update/doctor/profile")
    public ResponseEntity<?> updateDoctor(HttpServletRequest request, @RequestBody DoctorSignUpDTO doctorSignUpDTO) {
        AppUser appUser = userService.returnUser(request);

        Long appUserId = appUser.getId();

        var roles = appUser.getRoles();

        String password = appUser.getPassword();

        SignUpDTO signUpDTO = modelMapper.map(doctorSignUpDTO.getAppUser(), SignUpDTO.class);

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

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(appUserId);

        if (optionalDoctor.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"), HttpStatus.NOT_FOUND);
        }

        Doctor doctor = optionalDoctor.get();

        Long doctorId = doctor.getId();

        Long balance = doctor.getBalance();

        List<DoctorAvailableTime> doctorAvailableTimes = doctor.getAvailableTimes();

        List<DoctorOnlineAvailableTime> doctorOnlineAvailableTimes = doctor.getAvailableOnlineTimes();

        doctor = modelMapper.map(doctorSignUpDTO, Doctor.class);

        doctor.setId(doctorId);
        doctor.setBalance(balance);
        doctor.setAppUser(appUser);
        doctor.setAvailableTimes(doctorAvailableTimes);
        doctor.setAvailableOnlineTimes(doctorOnlineAvailableTimes);
        doctorRepository.save(doctor);

        return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @PostMapping("/update/doctor/offline")
    public ResponseEntity<?> updateDoctorOffline(HttpServletRequest request, @RequestBody DoctorUpdateScheduleDTO doctorUpdateScheduleDTO) {
        AppUser user = userService.returnUser(request);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(user.getId());

        if (optionalDoctor.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"), HttpStatus.NOT_FOUND);
        }

        Doctor doctor = optionalDoctor.get();

        List<DoctorAvailableTime> oldAvailableTimes = doctor.getAvailableTimes();

        for (int i = 0; i < doctorUpdateScheduleDTO.getAvailableTimes().size(); i++) {
            doctorUpdateScheduleDTO.getAvailableTimes().get(i).setId(null);
            doctorUpdateScheduleDTO.getAvailableTimes().get(i).setCount(0);
            doctorUpdateScheduleDTO.getAvailableTimes().get(i).setAvailTime(doctorUpdateScheduleDTO.getAvailableTimes().get(i).getStartTime());
            doctorUpdateScheduleDTO.getAvailableTimes().get(i)
                    .setDate(DoctorService.currentDate(doctorUpdateScheduleDTO.getAvailableTimes().get(i).getDay()));
        }

        doctor.setAvailableTimes(doctorUpdateScheduleDTO.getAvailableTimes());

        doctorAvailableTimeRepository.deleteAll(oldAvailableTimes);

        doctorRepository.save(doctor);

        return new ResponseEntity<>(ApiResponse.create("update", "Doctor offline time updated"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @PostMapping("/update/doctor/online")
    public ResponseEntity<?> updateDoctorOnline(HttpServletRequest request, @RequestBody DoctorUpdateScheduleDTO doctorUpdateScheduleDTO) {
        AppUser user = userService.returnUser(request);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(user.getId());

        if (optionalDoctor.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"), HttpStatus.NOT_FOUND);
        }

        Doctor doctor = optionalDoctor.get();

        List<DoctorOnlineAvailableTime> oldOnlineAvailableTimes = doctor.getAvailableOnlineTimes();

        for (int i = 0; i < doctorUpdateScheduleDTO.getAvailableOnlineTimes().size(); i++) {
            doctorUpdateScheduleDTO.getAvailableOnlineTimes().get(i).setId(null);
            doctorUpdateScheduleDTO.getAvailableOnlineTimes().get(i).setCount(0);
            doctorUpdateScheduleDTO.getAvailableOnlineTimes().get(i).setAvailTime(doctorUpdateScheduleDTO.getAvailableOnlineTimes().get(i).getStartTime());
            doctorUpdateScheduleDTO.getAvailableOnlineTimes().get(i)
                    .setDate(DoctorService.currentDate(doctorUpdateScheduleDTO.getAvailableOnlineTimes().get(i).getDay()));
        }

        doctor.setAvailableOnlineTimes(doctorUpdateScheduleDTO.getAvailableOnlineTimes());

        doctorOnlineAvailableTimeRepository.deleteAll(oldOnlineAvailableTimes);

        doctorRepository.save(doctor);

        return new ResponseEntity<>(ApiResponse.create("update", "Doctor offline time updated"), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR','USER')")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<?> showDoctorDetails(@PathVariable Long id) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(id);
        Doctor doctor = new Doctor();
        if (optionalDoctor.isPresent()) {
            doctor = optionalDoctor.get();
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"), HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {

            if (doctor.getAvailableTimes().get(i).getDate().isBefore(LocalDate.now())) {
                doctor.getAvailableTimes().get(i)
                        .setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                doctor.getAvailableTimes().get(i).setCount(0);
            }
        }

        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            if (doctor.getAvailableOnlineTimes().get(i).getDate().isBefore(LocalDate.now())) {
                doctor.getAvailableOnlineTimes().get(i)
                        .setDate(DoctorService.nextDate(doctor.getAvailableOnlineTimes().get(i).getDay()));
                doctor.getAvailableOnlineTimes().get(i).setCount(0);
            }
        }

        doctorService.setSerialTime(doctor);

        doctorRepository.save(doctor);

        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);

        doctorDTO.setId(doctor.getAppUser().getId());
        doctorDTO.setFirstName(doctor.getAppUser().getFirstName());
        doctorDTO.setLastName(doctor.getAppUser().getLastName());
        doctorDTO.setEmail(doctor.getAppUser().getEmail());
        doctorDTO.setContactNo(doctor.getAppUser().getContactNo());
        doctorDTO.setDp(doctor.getAppUser().getDp());
        doctorDTO.setRating(reviewRepository.findAvgRating(id));

        doctorDTO.setReviewCount(reviewRepository.findCount(id));

        if (doctorDTO.getRating() == null) {
            doctorDTO.setRating(0.0);
        }

        if (doctorDTO.getReviewCount() == null) {
            doctorDTO.setReviewCount(0L);
        }
        return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR','USER')")
    @GetMapping("/doctor/all")
    public ResponseEntity<?> showAllDoctorDetails() {

        List<Doctor> allDoctors = doctorService.getAllDoctors();

        if (allDoctors.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "No Doctor Found"), HttpStatus.BAD_REQUEST);
        }

        for (int it = 0; it < allDoctors.size(); it++) {
            Doctor doctor = allDoctors.get(it);

            for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {

                if (doctor.getAvailableTimes().get(i).getDate().isBefore(LocalDate.now())) {
                    doctor.getAvailableTimes().get(i)
                            .setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                    doctor.getAvailableTimes().get(i).setCount(0);
                }
            }

            for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

                if (doctor.getAvailableOnlineTimes().get(i).getDate().isBefore(LocalDate.now())) {
                    doctor.getAvailableOnlineTimes().get(i)
                            .setDate(DoctorService.nextDate(doctor.getAvailableOnlineTimes().get(i).getDay()));
                    doctor.getAvailableOnlineTimes().get(i).setCount(0);
                }
            }

            doctorService.setSerialTime(doctor);

            doctorRepository.save(doctor);

        }

        List<DoctorDTO> allDoctorsDTO = new ArrayList<>();

        for (int i = 0; i < allDoctors.size(); i++) {
            allDoctorsDTO.add(modelMapper.map(allDoctors.get(i), DoctorDTO.class));
            allDoctorsDTO.get(i).setId(allDoctors.get(i).getAppUser().getId());
            allDoctorsDTO.get(i).setFirstName(allDoctors.get(i).getAppUser().getFirstName());
            allDoctorsDTO.get(i).setLastName(allDoctors.get(i).getAppUser().getLastName());
            allDoctorsDTO.get(i).setEmail(allDoctors.get(i).getAppUser().getEmail());
            allDoctorsDTO.get(i).setContactNo(allDoctors.get(i).getAppUser().getContactNo());
            allDoctorsDTO.get(i).setDp(allDoctors.get(i).getAppUser().getDp());
            allDoctorsDTO.get(i).setRating(reviewRepository.findAvgRating(allDoctors.get(i).getAppUser().getId()));
            allDoctorsDTO.get(i).setReviewCount(reviewRepository.findCount(allDoctors.get(i).getAppUser().getId()));
            if (allDoctorsDTO.get(i).getRating() == null) {
                allDoctorsDTO.get(i).setRating(0.0);
            }

            if (allDoctorsDTO.get(i).getReviewCount() == null) {
                allDoctorsDTO.get(i).setReviewCount(0L);
            }
        }
        return new ResponseEntity<>(allDoctorsDTO, HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @PostMapping("/update/doctor/serial/pres/{id}")
    public ResponseEntity<?> updateDiagnosisReport(@RequestBody PrescriptionDTO prescriptionDTO,
                                                   @PathVariable(name = "id") Long id) {

        Optional<DoctorSerial> optionalDoctorSerial = doctorSerialRepository.findById(id);
        if (optionalDoctorSerial.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "DoctorSerial not Found"), HttpStatus.NOT_FOUND);
        }
        var doctorSerial = optionalDoctorSerial.get();
        doctorSerial.setPrescription(prescriptionDTO.getPrescription());
        doctorSerialRepository.save(doctorSerial);

        return new ResponseEntity<>(ApiResponse.create("update", "Prescription added"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @GetMapping("/dashboard/doctor/pending")
    public ResponseEntity<?> getAllPendingPrescriptions(HttpServletRequest request) {
        AppUser doctor = userService.returnUser(request);

        List<DoctorSerial> pendingDoctorSerials = doctorSerialRepository
                .findByPrescriptionIsNullAndDoctorId(doctor.getId());

        if (pendingDoctorSerials.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending found"), HttpStatus.OK);
        }

        List<DoctorSerialViewDTO> doctorSerialViewDTOS = new ArrayList<>();

        for (var doctorSerial : pendingDoctorSerials) {
            DoctorSerialViewDTO doctorSerialViewDTO = new DoctorSerialViewDTO();
            doctorSerialViewDTO.setId(doctorSerial.getId());
            doctorSerialViewDTO.setTime(doctorSerial.getTime());
            doctorSerialViewDTO.setDate(doctorSerial.getAppointmentDate());
            doctorSerialViewDTO
                    .setPatientName(doctorSerial.getUser().getFirstName() + " " + doctorSerial.getUser().getLastName());
            doctorSerialViewDTO.setType(doctorSerial.getType());
            doctorSerialViewDTO.setPatientId(doctorSerial.getUser().getId());
            doctorSerialViewDTO.setContactNo(doctorSerial.getUser().getContactNo());

            doctorSerialViewDTOS.add(doctorSerialViewDTO);
        }

        return new ResponseEntity<>(doctorSerialViewDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @GetMapping("/dashboard/doctor/upcoming")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request) {
        AppUser doctor = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now(ZoneId.of("Asia/Dhaka")).minusMinutes(30));

        List<DoctorSerial> upcomingDoctorSerials = doctorSerialRepository.findByDateAndTimeAndDoctorId(LocalDate.now(),
                time, doctor.getId());

        if (upcomingDoctorSerials.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<DoctorSerialViewDTO> doctorSerialViewDTOS = new ArrayList<>();

        for (var doctorSerial : upcomingDoctorSerials) {
            DoctorSerialViewDTO doctorSerialViewDTO = new DoctorSerialViewDTO();
            doctorSerialViewDTO.setId(doctorSerial.getId());
            doctorSerialViewDTO.setTime(doctorSerial.getTime());
            doctorSerialViewDTO.setDate(doctorSerial.getAppointmentDate());
            doctorSerialViewDTO
                    .setPatientName(doctorSerial.getUser().getFirstName() + " " + doctorSerial.getUser().getLastName());
            doctorSerialViewDTO.setType(doctorSerial.getType());
            doctorSerialViewDTO.setPatientId(doctorSerial.getUser().getId());
            doctorSerialViewDTO.setContactNo(doctorSerial.getUser().getContactNo());

            doctorSerialViewDTOS.add(doctorSerialViewDTO);
        }

        return new ResponseEntity<>(doctorSerialViewDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @DeleteMapping("/delete/doctor/serial/{id}")
    public ResponseEntity<?> deleteUpcomingSerial(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        AppUser doctorUser = userService.returnUser(request);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(doctorUser.getId());

        if (optionalDoctor.isPresent()) {
            Optional<DoctorSerial> optionalDoctorSerial = doctorSerialRepository.findById(id);

            if (optionalDoctorSerial.isPresent()) {
                DoctorSerial doctorSerial = optionalDoctorSerial.get();
                Doctor doctor = optionalDoctor.get();

                doctor.balance -= (doctorSerial.getPrice() - 10);
                doctorSerial.setPrice(doctorSerial.getPrice() - 10);

                doctorRepository.save(doctor);

//                BkashRefundResponse bkashRefundResponse = bkashPaymentService.refundPayment(doctorSerial.getPaymentId(),
//                        doctorSerial.getTrxId(), doctorSerial.getPrice().toString());

                doctorSerialRepository.delete(doctorSerial);

                return new ResponseEntity<>(ApiResponse.create("delete","Doctor Serial is deleted"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ApiResponse.create("error", "Doctor Serial not found"), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('USER','DOCTOR')")
    @GetMapping("/doctor/serial/check/{id}")
    public  ResponseEntity<?>makeChecked(@PathVariable(name="id")Long id)
    {
        Optional<DoctorSerial>optionalDoctorSerial = doctorSerialRepository.findById(id);

        if(optionalDoctorSerial.isEmpty())
        {
            return  new ResponseEntity<>(ApiResponse.create("error", "Not found"),HttpStatus.NOT_FOUND);
        }

        DoctorSerial doctorSerial = optionalDoctorSerial.get();
        doctorSerial.setChecked(1);
        doctorSerial.setReviewChecked(0);
        doctorSerialRepository.save(doctorSerial);

        return new ResponseEntity<>(ApiResponse.create("checked","DoctorSerial is now checked"),HttpStatus.OK);
    }

}
