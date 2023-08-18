package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorSignUpDTO;
import com.healthtechbd.backend.dto.JWTDTO;
import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.AmbulanceProviderRepository;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.repo.RoleRepository;
import com.healthtechbd.backend.security.AppUserServiceSecurity;
import com.healthtechbd.backend.security.JWTService;
import com.healthtechbd.backend.service.DoctorService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import org.antlr.v4.runtime.misc.LogManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserServiceSecurity userServiceSecurity;

    @Autowired
    private AmbulanceProviderRepository ambulanceProviderRepository;

    @Autowired
    private PasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DoctorRepository doctorRepository;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateAppUser(@RequestBody SignInDTO signInDTO) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInDTO.getEmail(),
                            signInDTO.getPassword()
                    )
            );
        } catch (Exception e) {

            ApiResponse errorResponse = ApiResponse.create("error", "Invaild user email or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userServiceSecurity.loadUserByUsername(signInDTO.getEmail());
        Optional<AppUser> optionalAppUser = userRepository.findByEmail(signInDTO.getEmail());
        AppUser appUser = new AppUser();
        if (optionalAppUser.isPresent()) {
            appUser = optionalAppUser.get();
        }
        String token = jwtService.generateToken(userDetails);
        JWTDTO jwtdto = new JWTDTO(token, appUser.getId());


        return new ResponseEntity<>(jwtdto, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerAppUser(@RequestBody SignUpDTO signUpDTO) {
        RegistrationResponse response = userService.registerUser(signUpDTO, "USER");
        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }
        return ResponseEntity.ok(response.getResponse());
    }

    @PostMapping("/doctor_registration")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorSignUpDTO doctorSignUpDTO) {

        SignUpDTO signUpDTO = modelMapper.map(doctorSignUpDTO.getAppUser(), SignUpDTO.class);

        RegistrationResponse response = userService.registerUser(signUpDTO, "DOCTOR");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }

        doctorSignUpDTO.setAppUser(response.getUser());

        Doctor doctor = modelMapper.map(doctorSignUpDTO, Doctor.class);


        doctor.setAvailableTimes(new ArrayList<DoctorAvailableTime>());

        for (int i = 0; i < doctorSignUpDTO.getDays().size(); i++) {
            DoctorAvailableTime doctorAvailableTime = new DoctorAvailableTime();
            doctorAvailableTime.setDay(doctorSignUpDTO.getDays().get(i));
            doctorAvailableTime.setDate(DoctorService.currentDate(doctorSignUpDTO.getDays().get(i)));
            doctorAvailableTime.setStartTime(doctorSignUpDTO.getTimes().get(0));
            doctorAvailableTime.setEndTime(doctorSignUpDTO.getTimes().get(1));
            doctorAvailableTime.setAvailTime(0.0);
            doctorAvailableTime.setCount(0);

            doctor.getAvailableTimes().add(doctorAvailableTime);

        }

        doctor.setAvailableOnlineTimes(new ArrayList<DoctorOnlineAvailableTime>());

        for (int i = 0; i < doctorSignUpDTO.getOnlineDays().size(); i++) {
            DoctorOnlineAvailableTime doctorOnlineAvailableTime = new DoctorOnlineAvailableTime();
            doctorOnlineAvailableTime.setDay(doctorSignUpDTO.getDays().get(i));
            doctorOnlineAvailableTime.setDate(DoctorService.currentDate(doctorSignUpDTO.getDays().get(i)));
            doctorOnlineAvailableTime.setOnlineStartTime(doctorSignUpDTO.getTimes().get(2));
            doctorOnlineAvailableTime.setOnlineEndTime(doctorSignUpDTO.getTimes().get(3));
            doctorOnlineAvailableTime.setOnlineAvailTime(0.0);
            doctorOnlineAvailableTime.setOnlineCount(0);

            doctor.getAvailableOnlineTimes().add(doctorOnlineAvailableTime);

        }

        Doctor savedDoctor = doctorRepository.save(doctor);
        return new ResponseEntity<>(ApiResponse.create("create", "Doctor signup successful"), HttpStatus.OK);
    }

    @PostMapping("/ambulanceProvider_registration")
    public ResponseEntity<?> registerAmbulanceProvider(@RequestBody AmbulanceProvider ambulanceProvider)
    {

        SignUpDTO signUpDTO = modelMapper.map(ambulanceProvider.getAppUser(), SignUpDTO.class);

        RegistrationResponse response = userService.registerUser(signUpDTO, "AMBULANCE");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }

        ambulanceProvider.setAppUser(response.getUser());

        for(int i=0;i<ambulanceProvider.getAmbulances().size();i++)
        {
            ambulanceProvider.getAmbulances().get(i).setAmbulanceProvider(ambulanceProvider);
            ambulanceProvider.getAmbulances().get(i).setAppUser(ambulanceProvider.getAppUser());

        }


        ambulanceProviderRepository.save(ambulanceProvider);


        return new ResponseEntity<>(ApiResponse.create("create","Sign up Successful"),HttpStatus.OK);



    }


}

