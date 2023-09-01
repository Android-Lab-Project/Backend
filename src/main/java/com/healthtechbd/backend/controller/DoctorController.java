package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorDTO;
import com.healthtechbd.backend.dto.DoctorSerialViewDTO;
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
import com.healthtechbd.backend.utils.BkashPaymentService;
import com.healthtechbd.backend.utils.BkashRefundResponse;
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

    @GetMapping("/delete/doctor/serial/{id}")
    public ResponseEntity<?>deleteUpcomingSerial(@PathVariable(name="id") Long id, HttpServletRequest request)
    {
        AppUser doctorUser = userService.returnUser(request);

        Optional<Doctor>optionalDoctor = doctorRepository.findByAppUser_Id(doctorUser.getId());

        Optional<DoctorSerial>optionalDoctorSerial = doctorSerialRepository.findById(id);

        DoctorSerial doctorSerial =optionalDoctorSerial.get();

        optionalDoctor.get().balance-=doctorSerial.getPrice();

        doctorRepository.save(optionalDoctor.get());

        BkashRefundResponse bkashRefundResponse = bkashPaymentService.refundPayment(doctorSerial.getPaymentId(),doctorSerial.getTrxId(),doctorSerial.getPrice().toString());

        doctorSerialRepository.delete(doctorSerial);

        return new ResponseEntity<>(bkashRefundResponse,HttpStatus.OK);
    }



}
