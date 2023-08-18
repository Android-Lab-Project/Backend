package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorSignUpDTO;
import com.healthtechbd.backend.dto.JWTDTO;
import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.repo.RoleRepository;
import com.healthtechbd.backend.security.AppUserServiceSecurity;
import com.healthtechbd.backend.security.JWTService;
import com.healthtechbd.backend.service.DoctorService;
import com.healthtechbd.backend.utils.ApiResponse;
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
    private PasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private JWTService jwtService;

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
    public ResponseEntity<?> registerAppUser(@RequestBody SignUpDTO signupDTO) {
        ApiResponse errorResponse = new ApiResponse();

        if (signupDTO.getFirstName() == null || signupDTO.getFirstName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "First Name can not be empty");

        if (signupDTO.getLastName() == null || signupDTO.getLastName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Last Name can not be empty");

        if (signupDTO.getEmail() == null || signupDTO.getEmail().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Email can not be empty");

        if (signupDTO.getPassword() == null || signupDTO.getPassword().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Password can not be empty");

        if (userRepository.existsByEmail(signupDTO.getEmail()))
            errorResponse = ApiResponse.create("error", "Email is already taken!");

        if (!errorResponse.empty()) {
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String password = bcryptPasswordEncoder.encode(signupDTO.getPassword());
        signupDTO.setPassword(password);

        AppUser user = modelMapper.map(signupDTO, AppUser.class);
        user.setAccountVerified(true);

        Role role = new Role();
        role.setRoleType("USER");
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);


        ApiResponse createResponse = ApiResponse.create("create", "Sign up Successful");

        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

    @PostMapping("/doctor_registration")
    public ResponseEntity<?> saveDoctor(@RequestBody DoctorSignUpDTO doctorSignUpDTO) {
        ApiResponse errorResponse = new ApiResponse();

        if (doctorSignUpDTO.getAppUser() == null || doctorSignUpDTO.getAppUser().getFirstName() == null ||
                doctorSignUpDTO.getAppUser().getFirstName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "First Name can not be empty");

        if (doctorSignUpDTO.getAppUser() == null || doctorSignUpDTO.getAppUser().getLastName() == null ||
                doctorSignUpDTO.getAppUser().getLastName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Last Name can not be empty");

        if (doctorSignUpDTO.getAppUser() == null || doctorSignUpDTO.getAppUser().getEmail() == null ||
                doctorSignUpDTO.getAppUser().getEmail().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Email can not be empty");

        if (doctorSignUpDTO.getAppUser() == null || doctorSignUpDTO.getAppUser().getPassword() == null ||
                doctorSignUpDTO.getAppUser().getPassword().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Password can not be empty");

        if (userRepository.existsByEmail(doctorSignUpDTO.getAppUser().getEmail()))
            errorResponse = ApiResponse.create("error", "Email is already taken!");

        if (!errorResponse.empty()) {
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Role role = new Role("DOCTOR");
        doctorSignUpDTO.getAppUser().setRoles(Collections.singleton(role));

        Doctor doctor = modelMapper.map(doctorSignUpDTO, Doctor.class);

        String password = bcryptPasswordEncoder.encode(doctor.getAppUser().getPassword());
        doctor.getAppUser().setPassword(password);

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
    public ResponseEntity<?> saveAmbulanceProvider(@RequestBody AmbulanceProvider ambulanceProvider)
    {





    }


}

