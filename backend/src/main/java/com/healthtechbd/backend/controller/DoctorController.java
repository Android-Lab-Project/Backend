package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.service.DoctorService;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class DoctorController {

    @Autowired
    public DoctorRepository doctorRepository;

    @GetMapping("/dashboard/doctor/{id}")
    public ResponseEntity<?> showDoctorDetails(@PathVariable Long id)
    {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);
        Doctor doctor = new Doctor();
        if(optionalDoctor.isPresent())
        {
            doctor = optionalDoctor.get();
        }
        else
        {
            return new ResponseEntity<>("Doctor not found", HttpStatus.BAD_REQUEST);
        }




        doctorRepository.save(doctor);

        return new ResponseEntity<>(doctor,HttpStatus.OK);
    }


}
