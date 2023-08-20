package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceDTO;
import com.healthtechbd.backend.entity.Ambulance;
import com.healthtechbd.backend.repo.AmbulanceRepository;
import com.healthtechbd.backend.utils.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class AmbulanceController {

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/ambulance/all")
    public ResponseEntity<?> showAllAmbulanceDetails() {
        List<Ambulance> ambulances = ambulanceRepository.findAll();

        if (ambulances.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "No Ambulance Found"), HttpStatus.BAD_REQUEST);
        }

        List<AmbulanceDTO> ambulanceDTOS = new ArrayList<>();

        for (int i = 0; i < ambulances.size(); i++) {
            ambulanceDTOS.add(modelMapper.map(ambulances.get(i), AmbulanceDTO.class));
            ambulanceDTOS.get(i).setAppUser_id(ambulances.get(i).getAmbulanceProvider().getAppUser().getId());
        }

        return new ResponseEntity<>(ambulanceDTOS, HttpStatus.OK);
    }


}
