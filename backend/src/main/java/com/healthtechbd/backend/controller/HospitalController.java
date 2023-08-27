package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DiagnosisViewDTO;
import com.healthtechbd.backend.dto.StatisticsDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.DiagnosisOrder;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DiagnosisOrderRepository;
import com.healthtechbd.backend.service.TimeService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class HospitalController {


    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @GetMapping("/dashboard/hospital/pending")
    public ResponseEntity<?>getAllPendingReports(HttpServletRequest request)
    {
        AppUser hospital = userService.returnUser(request);

        List<DiagnosisOrder>diagnosisOrders = diagnosisOrderRepository.findByReportURLIsNullAndHospitalId(hospital.getId());

        if(diagnosisOrders.size()==0)
        {
            return new ResponseEntity<>(ApiResponse.create("empty","No pending found"),HttpStatus.OK);
        }

        List<DiagnosisViewDTO> diagnosisViewDTOS = new ArrayList<>();

        for(var i:diagnosisOrders)
        {
            DiagnosisViewDTO diagnosisViewDTO = new DiagnosisViewDTO();
            diagnosisViewDTO.setId(i.getId());
            diagnosisViewDTO.setDescription(i.getDescription());
            diagnosisViewDTO.setTime(i.getTime());
            diagnosisViewDTO.setPlace(i.getPlace());
            diagnosisViewDTO.setPatientName(i.getUser().getFirstName()+" "+i.getUser().getLastName());

            diagnosisViewDTOS.add(diagnosisViewDTO);
        }

        return new ResponseEntity<>(diagnosisViewDTOS,HttpStatus.OK);
    }

    @GetMapping("/dashboard/hospital/upcoming")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request)
    {
        AppUser hospital = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now());

        List<DiagnosisOrder>diagnosisOrders=diagnosisOrderRepository.findByDateAndTimeAndHospitalId(LocalDate.now(),time, hospital.getId());

        if(diagnosisOrders.size()==0)
        {
            return new ResponseEntity<>(ApiResponse.create("empty","No upcoming found"),HttpStatus.OK);
        }

        List<DiagnosisViewDTO> diagnosisViewDTOS = new ArrayList<>();

        for(var i:diagnosisOrders)
        {
            DiagnosisViewDTO diagnosisViewDTO = new DiagnosisViewDTO();
            diagnosisViewDTO.setId(i.getId());
            diagnosisViewDTO.setDescription(i.getDescription());
            diagnosisViewDTO.setTime(i.getTime());
            diagnosisViewDTO.setPlace(i.getPlace());
            diagnosisViewDTO.setPatientName(i.getUser().getFirstName()+" "+i.getUser().getLastName());

            diagnosisViewDTOS.add(diagnosisViewDTO);
        }

        return new ResponseEntity<>(diagnosisViewDTOS,HttpStatus.OK);


    }





}
