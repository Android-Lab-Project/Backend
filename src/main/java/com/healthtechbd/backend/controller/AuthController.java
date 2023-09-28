package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.ForgetPasswordDTO;
import com.healthtechbd.backend.dto.JWTDTO;
import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.Admin;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.TwoStepVerificationCode;
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

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
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
    private TwoStepVerificationCodeRepository twoStepVerificationCodeRepository;

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

        if (!userService.checkBan(signInDTO.getEmail())) {
            return new ResponseEntity<>(ApiResponse.create("ban", "Pharmacy is banned"), HttpStatus.BAD_REQUEST);
        }

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
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "User not found"), HttpStatus.NOT_FOUND);
        }
        String token = jwtService.generateToken(userDetails);
        JWTDTO jwtdto = new JWTDTO();
        jwtdto.setAccessToken(token);
        jwtdto.setId(appUser.getId());
        jwtdto.setEmail(appUser.getEmail());
        jwtdto.setTokenType("Bearer");
        jwtdto.setRole(appUser.getRoles().get(0).getRoleType());

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

        String password = user.getPassword();

        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        user = updateUserResponse.getUser();

        if (user.getPassword() == null) {
            user.setPassword(password);
        }

        user.setId(id);

        user.setId(id);

        user.setRoles(roles);

        userRepository.save(user);

        return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
    }

    @PostMapping("add/code")
    public ResponseEntity<?> addCode(@RequestBody TwoStepVerificationCode twoStepVerificationCode) {
        Optional<TwoStepVerificationCode> optionalTwoStepVerificationCode = twoStepVerificationCodeRepository.findByEmail(twoStepVerificationCode.getEmail());

        if (optionalTwoStepVerificationCode.isEmpty()) {
            twoStepVerificationCodeRepository.save(twoStepVerificationCode);
            return new ResponseEntity<>(ApiResponse.create("create", "code is inserted"), HttpStatus.OK);
        }

        Long id = optionalTwoStepVerificationCode.get().getId();

        twoStepVerificationCode.setId(id);

        twoStepVerificationCodeRepository.save(twoStepVerificationCode);

        return new ResponseEntity<>(ApiResponse.create("update", "code is updated"), HttpStatus.OK);
    }

    @PostMapping("match/code")
    public ResponseEntity<?> matchCode(@RequestBody TwoStepVerificationCode twoStepVerificationCode) {
        Optional<TwoStepVerificationCode> optionalTwoStepVerificationCode = twoStepVerificationCodeRepository.findByEmail(twoStepVerificationCode.getEmail());

        if (optionalTwoStepVerificationCode.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "No user found with this email"), HttpStatus.NOT_FOUND);
        }

        if (optionalTwoStepVerificationCode.get().getCode().equals(twoStepVerificationCode.getCode())) {
            return new ResponseEntity<>(ApiResponse.create("matched", "User code is matched"), HttpStatus.OK);
        }

        return new ResponseEntity<>(ApiResponse.create("error", "User code is mismatched"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/user/forgetPassword")
    public ResponseEntity<?> getForgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        Optional<AppUser> optionalAppUser = userRepository.findByEmail(forgetPasswordDTO.getEmail());

        if (optionalAppUser.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "No user with this email"), HttpStatus.NOT_FOUND);
        }

        AppUser user = optionalAppUser.get();

        String newPassword = bcryptPasswordEncoder.encode(forgetPasswordDTO.getPassword());

        user.setPassword(newPassword);

        userRepository.save(user);

        return new ResponseEntity<>(ApiResponse.create("update", "Password is updated"), HttpStatus.OK);
    }
}
