package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.JWTDTO;
import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.Admin;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.security.AppUserServiceSecurity;
import com.healthtechbd.backend.security.JWTService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import com.healthtechbd.backend.utils.UpdateUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RestController
public class AuthController {

    @Value("${app.admin.email}")
    private String adminEmail;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepository;

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
                            signInDTO.getPassword()));
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
        JWTDTO jwtdto = new JWTDTO(token, appUser.getId(), appUser.getRoles().get(0).getRoleType());

        return new ResponseEntity<>(jwtdto, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerAppUser(@RequestBody SignUpDTO signUpDTO) {

        RegistrationResponse response;

        if (signUpDTO.getEmail().equals(adminEmail)) {
            response = userService.registerUser(signUpDTO, "ADMIN");

            Admin admin = new Admin();
            admin.setAppUser(response.getUser());
            admin.setBalance(0L);

            if (response.getResponse().haveError()) {
                return ResponseEntity.badRequest().body(response.getResponse());
            }
            userRepository.save(response.getUser());

            adminRepository.save(admin);

            userService.AddUserCount(LocalDate.now());

            return ResponseEntity.ok(response.getResponse());

        } else {
            response = userService.registerUser(signUpDTO, "USER");
        }

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }
        userRepository.save(response.getUser());

        userService.AddUserCount(LocalDate.now());

        return ResponseEntity.ok(response.getResponse());
    }

    @PostMapping("update/user")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody SignUpDTO signUpDTO) {
        AppUser user = userService.returnUser(request);

        Long id = user.getId();

        var roles = user.getRoles();

        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        user = updateUserResponse.getUser();

        user.setId(id);

        user.setId(id);

        user.setRoles(roles);

        userRepository.save(user);

        return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);

    }
}
