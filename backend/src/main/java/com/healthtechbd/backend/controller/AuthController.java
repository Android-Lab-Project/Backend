package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorSignUpDTO;
import com.healthtechbd.backend.dto.JWTDTO;
import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.security.AppUserServiceSecurity;
import com.healthtechbd.backend.security.JWTService;
import com.healthtechbd.backend.service.DoctorService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.List;
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
    private MedicineRepository medicineRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

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
        userRepository.save(response.getUser());
        return ResponseEntity.ok(response.getResponse());
    }

    @PostMapping("/register/doctor")
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

    @PostMapping("/register/ambulanceProvider")
    public ResponseEntity<?> registerAmbulanceProvider(@RequestBody AmbulanceProvider ambulanceProvider) {

        SignUpDTO signUpDTO = modelMapper.map(ambulanceProvider.getAppUser(), SignUpDTO.class);

        RegistrationResponse response = userService.registerUser(signUpDTO, "AMBULANCE");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }

        ambulanceProvider.setAppUser(response.getUser());


        ambulanceProviderRepository.save(ambulanceProvider);


        return new ResponseEntity<>(ApiResponse.create("create", "Sign up Successful"), HttpStatus.OK);


    }


    @PostMapping("/register/pharmacy")
    public ResponseEntity<?> registerPharmacy(@RequestBody Pharmacy pharmacy) {
        SignUpDTO signUpDTO = modelMapper.map(pharmacy.getAppUser(), SignUpDTO.class);
        RegistrationResponse response = userService.registerUser(signUpDTO, "PHARMACY");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }
        pharmacy.setAppUser(response.getUser());

        pharmacyRepository.save(pharmacy);

        return new ResponseEntity<>(ApiResponse.create("create", "Sign up successful"), HttpStatus.OK);
    }

    @PostMapping("/register/hospital")
    public ResponseEntity<?> registerHospital(@RequestBody Hospital hospital) {
        SignUpDTO signUpDTO = modelMapper.map(hospital.getAppUser(), SignUpDTO.class);
        RegistrationResponse response = userService.registerUser(signUpDTO, "HOSPITAL");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }
        if (hospital.getHospitalName() == null || hospital.getHospitalName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital name is empty"), HttpStatus.BAD_REQUEST);
        }
        hospital.setAppUser(response.getUser());

        for (int i = 0; i < hospital.getDiagnosisList().size(); i++) {
            hospital.getDiagnosisList().get(i).setHospital(hospital);
        }

        hospitalRepository.save(hospital);

        return new ResponseEntity<>(ApiResponse.create("create", "Sign up successful"), HttpStatus.OK);
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {

        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
        }

        Long id = appUser.getId();

        List<Role> roles = appUser.getRoles();


        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getRoleType().equals("USER")) {
                userRepository.delete(appUser);
            }
            if (roles.get(i).getRoleType().equals("DOCTOR")) {

                Optional<Doctor> doctor = doctorRepository.findByAppUser_Id(id);

                if (!doctor.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }
                doctorRepository.delete(doctor.get());
            }
            if (roles.get(i).getRoleType().equals("HOSPITAL")) {
                Optional<Hospital> hospital = hospitalRepository.findByAppUser_Id(id);

                if (!hospital.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }

                hospitalRepository.delete(hospital.get());
            }
            if (roles.get(i).getRoleType().equals("PHARMACY")) {
                Optional<Pharmacy> pharmacy = pharmacyRepository.findByAppUser_Id(id);

                if (!pharmacy.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }

                pharmacyRepository.delete(pharmacy.get());
            }
            if (roles.get(i).getRoleType().equals("AMBULANCE")) {
                Optional<AmbulanceProvider> ambulanceProvider = ambulanceProviderRepository.findByAppUser_Id(id);

                if (!ambulanceProvider.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }

                ambulanceProviderRepository.delete(ambulanceProvider.get());
            }
        }

        return new ResponseEntity<>(ApiResponse.create("delete", "User is deleted"), HttpStatus.OK);
    }


}

