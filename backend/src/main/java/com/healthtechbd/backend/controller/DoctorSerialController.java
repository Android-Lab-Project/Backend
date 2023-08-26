package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorSerialDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.DoctorSerial;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DoctorSerialRepository;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class DoctorSerialController {
    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/doctorserial/create")
    public ResponseEntity<?> createDoctorSerial(@RequestBody DoctorSerialDTO doctorSerialDTO, HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Optional<AppUser> opDoctor = userRepository.findById(doctorSerialDTO.getDoctor_id());

        DoctorSerial doctorSerial = modelMapper.map(doctorSerialDTO, DoctorSerial.class);

        doctorSerial.setUser(user);
        doctorSerial.setDoctor(opDoctor.get());

        doctorSerialRepository.save(doctorSerial);

        return new ResponseEntity<>(ApiResponse.create("create", "doctor Serial added"), HttpStatus.OK);
    }

}
