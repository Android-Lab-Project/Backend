package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.PharmacyDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Pharmacy;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.MedicineOrderRepository;
import com.healthtechbd.backend.repo.PharmacyRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RestController
@PreAuthorize("hasAuthority('PHARMACY')")
public class PharmacyController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("permitAll()")
    @PostMapping("/register/pharmacy")
    public ResponseEntity<?> registerPharmacy(@RequestBody Pharmacy pharmacy) {
        SignUpDTO signUpDTO = modelMapper.map(pharmacy.getAppUser(), SignUpDTO.class);
        RegistrationResponse response = userService.registerUser(signUpDTO, "PHARMACY");

        if (response.getResponse().haveError()) {
            return ResponseEntity.badRequest().body(response.getResponse());
        }
        pharmacy.setAppUser(response.getUser());

        pharmacy.setBalance(0L);

        pharmacyRepository.save(pharmacy);

        userService.AddUserCount(LocalDate.now());

        return new ResponseEntity<>(ApiResponse.create("create", "Sign up successful"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/pharmacy/all")
    public ResponseEntity<?>getAllPharmacyDetails()
    {

        List<Pharmacy> pharmacyList = pharmacyRepository.findAll();

        List<PharmacyDTO>pharmacyDTOS = new ArrayList<>();
        for(var pharmacy: pharmacyList)
        {
            PharmacyDTO pharmacyDTO = modelMapper.map(pharmacy, PharmacyDTO.class);

            AppUser appUser = pharmacy.getAppUser();

            pharmacyDTO.setId(appUser.getId());
            pharmacyDTO.setFirstName(appUser.getFirstName());
            pharmacyDTO.setLastName(appUser.getLastName());
            pharmacyDTO.setEmail(appUser.getEmail());
            pharmacyDTO.setContactNo(appUser.getContactNo());
            pharmacyDTO.setDp(appUser.getDp());
            pharmacyDTO.setRating(reviewRepository.findAvgRating(appUser.getId()));
            pharmacyDTO.setReviewCount(reviewRepository.findCount(appUser.getId()));

            if (pharmacyDTO.getRating() == null) {
                pharmacyDTO.setRating(0.0);
            }

            if (pharmacyDTO.getReviewCount() == null) {
                pharmacyDTO.setReviewCount(0L);
            }

            pharmacyDTOS.add(pharmacyDTO);
        }

        return new ResponseEntity<>(pharmacyDTOS,HttpStatus.OK);

    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','PHARMACY')")
    @GetMapping("/pharmacy/{id}")
    public ResponseEntity<?>getPharmacyDetails(@PathVariable(name="id")Long id)
    {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByAppUser_Id(id);

        if(optionalPharmacy.isEmpty())
        {
            return  new ResponseEntity<>(ApiResponse.create("error","Pharmacy is not found"),HttpStatus.NOT_FOUND);
        }

        Pharmacy pharmacy = optionalPharmacy.get();

        PharmacyDTO pharmacyDTO = modelMapper.map(pharmacy, PharmacyDTO.class);

        pharmacyDTO.setId(id);
        pharmacyDTO.setFirstName(pharmacy.getAppUser().getFirstName());
        pharmacyDTO.setLastName(pharmacy.getAppUser().getLastName());
        pharmacyDTO.setEmail(pharmacy.getAppUser().getEmail());
        pharmacyDTO.setContactNo(pharmacy.getAppUser().getContactNo());
        pharmacyDTO.setDp(pharmacy.getAppUser().getDp());
        pharmacyDTO.setRating(reviewRepository.findAvgRating(id));
        pharmacyDTO.setReviewCount(reviewRepository.findCount(id));

        if (pharmacyDTO.getRating() == null) {
            pharmacyDTO.setRating(0.0);
        }

        if (pharmacyDTO.getReviewCount() == null) {
            pharmacyDTO.setReviewCount(0L);
        }

        return new ResponseEntity<>(pharmacyDTO,HttpStatus.OK);
    }


    @PostMapping("/update/pharmacy")
    public ResponseEntity<?> updatePharmacy(HttpServletRequest request, @RequestBody Pharmacy pharmacy) {
        AppUser appUser = userService.returnUser(request);
        Long appUserId = appUser.getId();
        var roles = appUser.getRoles();
        String password = appUser.getPassword();

        SignUpDTO signUpDTO = modelMapper.map(pharmacy.getAppUser(), SignUpDTO.class);
        UpdateUserResponse updateUserResponse = userService.updateUser(signUpDTO);

        if (updateUserResponse.getResponse().haveError()) {
            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.BAD_REQUEST);
        }

        appUser = updateUserResponse.getUser();
        if(appUser.getPassword()==null)
        {
            appUser.setPassword(password);
        }
        appUser.setId(appUserId);
        appUser.setRoles(roles);

        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByAppUser_Id(appUserId);

        if (optionalPharmacy.isPresent()) {
            Pharmacy existingPharmacy = optionalPharmacy.get();
            pharmacy.setId(existingPharmacy.getId());
            pharmacy.setBalance(existingPharmacy.getBalance());
            pharmacy.setAppUser(appUser);

            pharmacyRepository.save(pharmacy);

            return new ResponseEntity<>(updateUserResponse.getResponse(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Pharmacy not found"), HttpStatus.NOT_FOUND);
        }
    }
}
