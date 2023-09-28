package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceProviderDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AmbulanceProvider;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.AmbulanceProviderRepository;
import com.healthtechbd.backend.repo.ReviewRepository;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import com.healthtechbd.backend.utils.UpdateUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
@PreAuthorize("hasAuthority('AMBULANCE')")
public class AmbulanceProviderController {

    @Autowired
    private AmbulanceProviderRepository ambulanceProviderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("permitAll()")
    @PostMapping("/register/ambulanceProvider")
    public ResponseEntity<ApiResponse> registerAmbulanceProvider(@RequestBody AmbulanceProvider ambulanceProvider) {
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
    public ResponseEntity<ApiResponse> updateProvider(HttpServletRequest request,
                                                      @RequestBody AmbulanceProvider ambulanceProvider) {
        AppUser appUser = userService.returnUser(request);
        Long appUserId = appUser.getId();
        var roles = appUser.getRoles();
        String password = appUser.getPassword();

        SignUpDTO signUpDTO = modelMapper.map(ambulanceProvider.getAppUser(), SignUpDTO.class);
        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        if (updateUserResponse.getResponse().haveError()) {
            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.BAD_REQUEST);
        }

        appUser = updateUserResponse.getUser();
        if (appUser.getPassword() == null) {
            appUser.setPassword(password);
        }
        appUser.setId(appUserId);
        appUser.setRoles(roles);

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository.findByAppUser_Id(appUserId);

        if (optionalAmbulanceProvider.isPresent()) {
            AmbulanceProvider existingProvider = optionalAmbulanceProvider.get();
            ambulanceProvider.setId(existingProvider.getId());
            ambulanceProvider.setBalance(existingProvider.getBalance());
            ambulanceProvider.setAppUser(appUser);

            ambulanceProviderRepository.save(ambulanceProvider);

            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Ambulance provider not found"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('AMBULANCE','USER')")
    @GetMapping("/ambulanceProvider/{id}")
    public ResponseEntity<?> getProviderInfo(@PathVariable(name = "id") Long id) {
        Optional<AmbulanceProvider> optional = ambulanceProviderRepository.findByAppUser_Id(id);

        if (optional.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Provider not found"), HttpStatus.NOT_FOUND);
        }

        AmbulanceProvider provider = optional.get();

        AmbulanceProviderDTO ambulanceProviderDTO = modelMapper.map(optional.get(), AmbulanceProviderDTO.class);

        ambulanceProviderDTO.setId(id);
        ambulanceProviderDTO.setFirstName(provider.getAppUser().getFirstName());
        ambulanceProviderDTO.setLastName(provider.getAppUser().getLastName());
        ambulanceProviderDTO.setEmail(provider.getAppUser().getEmail());
        ambulanceProviderDTO.setContactNo(provider.getAppUser().getContactNo());
        ambulanceProviderDTO.setDp(provider.getAppUser().getDp());
        ambulanceProviderDTO.setRating(reviewRepository.findAvgRating(provider.getAppUser().getId()));
        ambulanceProviderDTO.setReviewCount(reviewRepository.findCount(provider.getAppUser().getId()));

        if (ambulanceProviderDTO.getRating() == null) {
            ambulanceProviderDTO.setRating(0.0);
        }

        if (ambulanceProviderDTO.getReviewCount() == null) {
            ambulanceProviderDTO.setReviewCount(0L);
        }

        return new ResponseEntity<>(ambulanceProviderDTO, HttpStatus.OK);
    }


}
