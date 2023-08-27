package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceDTO;
import com.healthtechbd.backend.entity.Ambulance;
import com.healthtechbd.backend.entity.AmbulanceProvider;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.AmbulanceProviderRepository;
import com.healthtechbd.backend.repo.AmbulanceRepository;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class AmbulanceController {

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private AmbulanceProviderRepository ambulanceProviderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/add/ambulance")
    public ResponseEntity<?>addAmbulance(@RequestBody AmbulanceDTO ambulanceDTO, HttpServletRequest request)
    {
        AppUser appUser = userService.returnUser(request);

        if(ambulanceDTO.getName()==null || ambulanceDTO.getName().trim().length()==0)
        {
            return new ResponseEntity<>(ApiResponse.create("error","Name can't be empty"),HttpStatus.BAD_REQUEST);
        }
        if(ambulanceDTO.getType()==null || ambulanceDTO.getType().trim().length()==0)
        {
            return new ResponseEntity<>(ApiResponse.create("error","Type can't be empty"),HttpStatus.BAD_REQUEST);
        }

        Ambulance ambulance = modelMapper.map(ambulanceDTO,Ambulance.class);

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository.findByAppUser_Id(appUser.getId());

        if(!optionalAmbulanceProvider.isPresent())
        {
            return  new ResponseEntity<>(ApiResponse.create("error","Provider not found"),HttpStatus.BAD_REQUEST);
        }

        ambulance.setAmbulanceProvider(optionalAmbulanceProvider.get());

        ambulanceRepository.save(ambulance);

        return new ResponseEntity<>(ApiResponse.create("create","Ambulance added"), HttpStatus.OK);
    }



}
