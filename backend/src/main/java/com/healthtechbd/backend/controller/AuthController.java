package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.JWTDTO;
import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.entity.Role;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invaild user email or password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userServiceSecurity.loadUserByUsername(signInDTO.getEmail());
        Optional<AppUser> optionalAppUser = userRepository.findByEmail(signInDTO.getEmail());
        AppUser appUser= new AppUser();
        if (optionalAppUser.isPresent()) {
            appUser = optionalAppUser.get();
        }
        String token = jwtService.generateToken(userDetails);
        JWTDTO jwtdto = new JWTDTO(token, appUser.getId());


        return new ResponseEntity<>(jwtdto, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerAppUser(@RequestBody SignUpDTO signupDTO) {
        Map<String, String> errorResponse = new HashMap<>();

        if (signupDTO.getFirstName() == null || signupDTO.getFirstName().trim().length() == 0)
            errorResponse.put("error", "First Name can not be empty");

        if (signupDTO.getLastName() == null || signupDTO.getLastName().trim().length() == 0)
            errorResponse.put("error", "Last Name can not be empty");

        if (signupDTO.getEmail() == null || signupDTO.getEmail().trim().length() == 0)
            errorResponse.put("error", "Email can not be empty");

        if (signupDTO.getPassword() == null || signupDTO.getPassword().trim().length() == 0)
            errorResponse.put("error", "Password can not be empty");

        if (userRepository.existsByEmail(signupDTO.getEmail()))
            errorResponse.put("error", "Email is already taken!");

        if (!errorResponse.isEmpty()) {
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

        Map<String, String> createResponse = new HashMap<>();
        createResponse.put("create", "Sign up Successful");

        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }


    @Autowired
    private DoctorRepository doctorRepository;


    @PostMapping("/doctor_registration")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        ApiResponse errorResponse = new ApiResponse();

        if (doctor.getAppUser() == null || doctor.getAppUser().getFirstName() == null ||
                doctor.getAppUser().getFirstName().trim().length() == 0)
            ApiResponse.create("error", "First Name can not be empty");

        if (doctor.getAppUser() == null || doctor.getAppUser().getLastName() == null ||
                doctor.getAppUser().getLastName().trim().length() == 0)
            ApiResponse.create("error", "Last Name can not be empty");

        if (doctor.getAppUser() == null || doctor.getAppUser().getEmail() == null ||
                doctor.getAppUser().getEmail().trim().length() == 0)
            ApiResponse.create("error", "Email can not be empty");

        if (doctor.getAppUser() == null || doctor.getAppUser().getPassword() == null ||
                doctor.getAppUser().getPassword().trim().length() == 0)
            ApiResponse.create("error", "Password can not be empty");

        if (!errorResponse.isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Role role = new Role("DOCTOR");
        doctor.getAppUser().setRoles(Collections.singleton(role));

        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {
            doctor.getAvailableTimes().get(i).setDate(DoctorService.currentDate(doctor.getAvailableTimes().get(i).getDay()));
            doctor.getAvailableTimes().get(i).setCount(0);
            doctor.getAvailableTimes().get(i).setOnlineCount(0);
        }

        Doctor savedDoctor = doctorRepository.save(doctor);
        return new ResponseEntity<>(ApiResponse.create("create", "Doctor signup successful"), HttpStatus.OK);
    }
}

