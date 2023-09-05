package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorDTO;
import com.healthtechbd.backend.dto.DoctorSerialViewDTO;
import com.healthtechbd.backend.dto.DoctorSignUpDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.entity.DoctorSerial;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.repo.DoctorSerialRepository;
import com.healthtechbd.backend.service.DoctorService;
import com.healthtechbd.backend.service.TimeService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.service.BkashPaymentService;
import com.healthtechbd.backend.utils.BkashRefundResponse;
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
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private BkashPaymentService bkashPaymentService;

    @Autowired
    private TimeService timeService;


    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorSignUpDTO doctorSignUpDTO) {

        SignUpDTO signUpDTO = modelMapper.map(doctorSignUpDTO.getAppUser(), SignUpDTO.class);

        RegistrationResponse response = userService.registerUser(signUpDTO, "DOCTOR");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }

        doctorSignUpDTO.setAppUser(response.getUser());

        Doctor doctor = modelMapper.map(doctorSignUpDTO, Doctor.class);

        for (int i = 0; i < doctor.getAvailableTimes().size(); i++)
        {
            doctor.getAvailableTimes().get(i).setId(null);
            doctor.getAvailableTimes().get(i).setCount(0);
            doctor.getAvailableTimes().get(i).setAvailTime(0.0);
            doctor.getAvailableTimes().get(i).setDate(DoctorService.currentDate(doctor.getAvailableTimes().get(i).getDay()));

        }
        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            doctor.getAvailableOnlineTimes().get(i).setId(null);
            doctor.getAvailableOnlineTimes().get(i).setCount(0);
            doctor.getAvailableOnlineTimes().get(i).setAvailTime(0.0);
            doctor.getAvailableOnlineTimes().get(i).setDate(DoctorService.currentDate(doctor.getAvailableOnlineTimes().get(i).getDay()));
        }

        doctor.setBalance(0L);

        doctorRepository.save(doctor);

        userService.AddUserCount(LocalDate.now());

        return new ResponseEntity<>(ApiResponse.create("create","Doctor Sign up successful"), HttpStatus.OK);
    }

    @PostMapping("update/doctor")
    public ResponseEntity<?>updateDoctor(HttpServletRequest request, DoctorSignUpDTO doctorSignUpDTO)
    {
        AppUser appUser = userService.returnUser(request);

        Long appUserId = appUser.getId();

        SignUpDTO signUpDTO = modelMapper.map(doctorSignUpDTO.getAppUser(), SignUpDTO.class);

        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        if(updateUserResponse.getResponse().haveError())
        {
            return new ResponseEntity<>(updateUserResponse.getResponse(),HttpStatus.BAD_REQUEST);
        }

        appUser = updateUserResponse.getUser();

        appUser.setId(appUserId);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(appUserId);

        Doctor doctor = optionalDoctor.get();

        Long doctorId = doctor.getId();

        doctor = modelMapper.map(doctorSignUpDTO,Doctor.class);

        doctor.setId(doctorId);
        doctor.setAppUser(appUser);

        doctorRepository.save(doctor);

        return new ResponseEntity<>(updateUserResponse.getResponse(),HttpStatus.OK);
    }



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
                doctor.getAvailableTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                doctor.getAvailableTimes().get(i).setCount(0);
            }
        }

        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            if (doctor.getAvailableOnlineTimes().get(i).getDate().isBefore(LocalDate.now())) {
                doctor.getAvailableOnlineTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                doctor.getAvailableOnlineTimes().get(i).setCount(0);
            }
        }


        DoctorService.setSerialTime(doctor);

        doctorRepository.save(doctor);

        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);

        doctorDTO.setId(doctor.getAppUser().getId());
        doctorDTO.setFirstName(doctor.getAppUser().getFirstName());
        doctorDTO.setLastName(doctor.getAppUser().getLastName());
        doctorDTO.setEmail(doctor.getAppUser().getEmail());
        doctorDTO.setContactNo(doctor.getAppUser().getContactNo());
        doctorDTO.setDp(doctor.getAppUser().getDp());

        return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
    }

    @GetMapping("/doctor/all")
    public ResponseEntity<?> showAllDoctorDetails() {
        List<Doctor> allDoctors = DoctorService.getAllDoctors();

        if (allDoctors.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "No Doctor Found"), HttpStatus.BAD_REQUEST);
        }

        for (int it = 0; it < allDoctors.size(); it++) {
            Doctor doctor = allDoctors.get(it);

            for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {

                if (doctor.getAvailableTimes().get(i).getDate().isBefore(LocalDate.now())) {
                    doctor.getAvailableTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                    doctor.getAvailableTimes().get(i).setCount(0);
                }
            }


            for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

                if (doctor.getAvailableOnlineTimes().get(i).getDate().isBefore(LocalDate.now())) {
                    doctor.getAvailableOnlineTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                    doctor.getAvailableOnlineTimes().get(i).setCount(0);
                }
            }


            DoctorService.setSerialTime(doctor);

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
        }
        return new ResponseEntity<>(allDoctorsDTO, HttpStatus.OK);

    }

    @GetMapping("/dashboard/doctor/pending")
    public ResponseEntity<?> getAllPendingPrescriptions(HttpServletRequest request) {
        AppUser doctor = userService.returnUser(request);

        List<DoctorSerial> pendingDoctorSerials = doctorSerialRepository.findByPrescriptionIsNullAndDoctorId(doctor.getId());

        if (pendingDoctorSerials.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending found"), HttpStatus.OK);
        }

        List<DoctorSerialViewDTO> doctorSerialViewDTOS = new ArrayList<>();

        for (var doctorSerial : pendingDoctorSerials) {
            DoctorSerialViewDTO doctorSerialViewDTO = new DoctorSerialViewDTO();
            doctorSerialViewDTO.setId(doctorSerial.getId());
            doctorSerialViewDTO.setTime(doctorSerial.getTime());
            doctorSerialViewDTO.setPatientName(doctorSerial.getUser().getFirstName() + " " + doctorSerial.getUser().getLastName());

            doctorSerialViewDTOS.add(doctorSerialViewDTO);
        }

        return new ResponseEntity<>(doctorSerialViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/dashboard/doctor/upcoming")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request) {
        AppUser doctor = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now());

        List<DoctorSerial> upcomingDoctorSerials = doctorSerialRepository.findByDateAndTimeAndDoctorId(LocalDate.now(), time, doctor.getId());

        if (upcomingDoctorSerials.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<DoctorSerialViewDTO> doctorSerialViewDTOS = new ArrayList<>();

        for (var doctorSerial : upcomingDoctorSerials) {
            DoctorSerialViewDTO doctorSerialViewDTO = new DoctorSerialViewDTO();
            doctorSerialViewDTO.setId(doctorSerial.getId());
            doctorSerialViewDTO.setTime(doctorSerial.getTime());
            doctorSerialViewDTO.setPatientName(doctorSerial.getUser().getFirstName() + " " + doctorSerial.getUser().getLastName());

            doctorSerialViewDTOS.add(doctorSerialViewDTO);
        }

        return new ResponseEntity<>(doctorSerialViewDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete/doctor/serial/{id}")
    public ResponseEntity<?>deleteUpcomingSerial(@PathVariable(name="id") Long id, HttpServletRequest request)
    {
        AppUser doctorUser = userService.returnUser(request);

        Optional<Doctor>optionalDoctor = doctorRepository.findByAppUser_Id(doctorUser.getId());

        Optional<DoctorSerial>optionalDoctorSerial = doctorSerialRepository.findById(id);

        DoctorSerial doctorSerial =optionalDoctorSerial.get();

        optionalDoctor.get().balance-=doctorSerial.getPrice()-10;

        doctorSerial.setPrice(doctorSerial.getPrice()-10);

        doctorRepository.save(optionalDoctor.get());

        BkashRefundResponse bkashRefundResponse = bkashPaymentService.refundPayment(doctorSerial.getPaymentId(),doctorSerial.getTrxId(),doctorSerial.getPrice().toString());

        doctorSerialRepository.delete(doctorSerial);

        return new ResponseEntity<>(bkashRefundResponse,HttpStatus.OK);
    }



}
