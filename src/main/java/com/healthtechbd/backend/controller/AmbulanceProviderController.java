package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AmbulanceProvider;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.AmbulanceProviderRepository;
import com.healthtechbd.backend.repo.AppUserRepository;
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
import java.util.Optional;
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class AmbulanceProviderController {

    @Autowired
    private AmbulanceProviderRepository ambulanceProviderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register/ambulanceProvider")
    public ResponseEntity<?> registerAmbulanceProvider(@RequestBody AmbulanceProvider ambulanceProvider) {

        SignUpDTO signUpDTO = modelMapper.map(ambulanceProvider.getAppUser(), SignUpDTO.class);

        RegistrationResponse response = userService.registerUser(signUpDTO, "AMBULANCE");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }

        ambulanceProvider.setAppUser(response.getUser());

        ambulanceProvider.setBalance(0L);


        ambulanceProviderRepository.save(ambulanceProvider);

        userService.AddUserCount(LocalDate.now());


        return new ResponseEntity<>(ApiResponse.create("create", "Sign up Successful"), HttpStatus.OK);


    }

    @PostMapping("/update/ambulanceProvider")
    public ResponseEntity<?> upateProvider(HttpServletRequest request, @RequestBody AmbulanceProvider ambulanceProvider) {
        AppUser appUser = userService.returnUser(request);

        Long appUserId = appUser.getId();

        SignUpDTO signUpDTO = modelMapper.map(ambulanceProvider.getAppUser(), SignUpDTO.class);

        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        if (updateUserResponse.getResponse().haveError()) {
            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.BAD_REQUEST);
        }

        appUser = updateUserResponse.getUser();

        appUser.setId(appUserId);

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository.findByAppUser_Id(appUserId);

        ambulanceProvider.setId(optionalAmbulanceProvider.get().getId());
        ambulanceProvider.setAppUser(appUser);

        ambulanceProviderRepository.save(ambulanceProvider);

        return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
    }

}
