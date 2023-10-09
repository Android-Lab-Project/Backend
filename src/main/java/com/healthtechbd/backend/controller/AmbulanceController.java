package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceDTO;
import com.healthtechbd.backend.dto.ProviderTripViewDTO;
import com.healthtechbd.backend.entity.Ambulance;
import com.healthtechbd.backend.entity.AmbulanceProvider;
import com.healthtechbd.backend.entity.AmbulanceTrip;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.BkashPaymentService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.AppConstants;
import com.healthtechbd.backend.utils.BkashCreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
public class AmbulanceController {

    @Autowired
    BkashPaymentService bkashPaymentService;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private AmbulanceRepository ambulanceRepository;
    @Autowired
    private AmbulanceProviderRepository ambulanceProviderRepository;
    @Autowired
    private AmbulanceTripRepository ambulanceTripRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('AMBULANCE')")
    @PostMapping("/add/ambulance")
    public ResponseEntity<?> addAmbulance(@RequestBody AmbulanceDTO ambulanceDTO, HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        if (ambulanceDTO.getName() == null || ambulanceDTO.getName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Name can't be empty"), HttpStatus.BAD_REQUEST);
        }
        if (ambulanceDTO.getType() == null || ambulanceDTO.getType().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Type can't be empty"), HttpStatus.BAD_REQUEST);
        }

        Ambulance ambulance = modelMapper.map(ambulanceDTO, Ambulance.class);

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository
                .findByAppUser_Id(appUser.getId());

        if (optionalAmbulanceProvider.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Provider not found"), HttpStatus.BAD_REQUEST);
        }

        ambulance.setAmbulanceProvider(optionalAmbulanceProvider.get());

        ambulanceRepository.save(ambulance);

        return new ResponseEntity<>(ApiResponse.create("create", "Ambulance added"), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('AMBULANCE','USER')")
    @GetMapping("/ambulance/{id}")
    public ResponseEntity<?> getAllAmbulances(@PathVariable(name = "id") Long id) {

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository.findByAppUser_Id(id);

        List<Ambulance> ambulances = ambulanceRepository
                .findByAmbulanceProvider_Id(optionalAmbulanceProvider.get().getId());

        if (ambulances.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Ambulance Found"), HttpStatus.OK);
        }

        List<AmbulanceDTO> ambulanceDTOS = new ArrayList<>();

        for (var i : ambulances) {
            AmbulanceDTO ambulanceDTO = modelMapper.map(i, AmbulanceDTO.class);
            ambulanceDTOS.add(ambulanceDTO);
        }

        return new ResponseEntity<>(ambulanceDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AMBULANCE')")
    @DeleteMapping("/delete/ambulance/{id}")
    public ResponseEntity<?> deleteDiagnosis(@PathVariable(name = "id") Long id) {
        try {
            ambulanceRepository.deleteById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.create("error", "Ambulance can't be deleted"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ApiResponse.create("delete", "Ambulance deleted"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AMBULANCE')")
    @GetMapping("/ambulance/trip/all")
    public ResponseEntity<?> getAllTrips(HttpServletRequest request) {
        AppUser provider = userService.returnUser(request);

        List<AmbulanceTrip> ambulanceTrips = ambulanceTripRepository.findByAmbulanceProviderIsNullAndNotBidder(provider.getId());
        if (ambulanceTrips.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Trip found"), HttpStatus.OK);
        }
        List<ProviderTripViewDTO> ambulanceTripViewDTOS = new ArrayList<>();

        for (var ambulanceTrip : ambulanceTrips) {
            ProviderTripViewDTO ambulanceTripViewDTO = new ProviderTripViewDTO();
            ambulanceTripViewDTO.setId(ambulanceTrip.getId());
            ambulanceTripViewDTO.setUserId(ambulanceTrip.getUser().getId());
            ambulanceTripViewDTO.setUserContactNo(ambulanceTrip.getUser().getContactNo());
            ambulanceTripViewDTO
                    .setUserName(ambulanceTrip.getUser().getFirstName() + " " + ambulanceTrip.getUser().getLastName());
            ambulanceTripViewDTO.setSource(ambulanceTrip.getSource());
            ambulanceTripViewDTO.setDestination(ambulanceTrip.getDestination());
            ambulanceTripViewDTO.setLocation(ambulanceTrip.getLocation());
            ambulanceTripViewDTO.setPrice(ambulanceTrip.getPrice());
            ambulanceTripViewDTO.setOrderDate(ambulanceTrip.getOrderDate());

            ambulanceTripViewDTOS.add(ambulanceTripViewDTO);
        }

        return new ResponseEntity<>(ambulanceTripViewDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AMBULANCE')")
    @GetMapping("/ambulance/trip/bid/{id}")
    public ResponseEntity<?> addBidderToTrip(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        AppUser bidder = userService.returnUser(request);

        Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(id);

        if (optionalAmbulanceTrip.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "trip not found"), HttpStatus.NOT_FOUND);
        }

        AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();

        List<AppUser> bidders = ambulanceTrip.getBidders() == null ? new ArrayList<>() : ambulanceTrip.getBidders();

        bidders.add(bidder);

        ambulanceTrip.setBidders(bidders);

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("update", "trip updated"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/ambulance/trip/reject/{id1}/{id2}")
    public ResponseEntity<?> deleteBidderFromTrip(@PathVariable(name = "id1") Long id1, @PathVariable(name = "id2") Long id2) {

        Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(id1);

        if (optionalAmbulanceTrip.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Trip not found"), HttpStatus.NOT_FOUND);
        }

        Optional<AppUser> optionalProvider = userRepository.findById(id2);

        if (optionalProvider.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Provider not found"), HttpStatus.NOT_FOUND);
        }

        AppUser bidder = optionalProvider.get();

        AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();

        List<AppUser> bidders = ambulanceTrip.getBidders();

        bidders.remove(bidder);

        ambulanceTrip.setBidders(bidders);

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("delete", "Bidder Rejected"), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('AMBULANCE','USER')")
    @GetMapping("update/ambulancetrip/{id1}/{id2}")
    public ResponseEntity<?> updateAmbulanceTrip(HttpServletRequest request, @PathVariable(name = "id1") Long id1,
                                                 @PathVariable(name = "id2") Long id2) {
        Optional<AmbulanceProvider> optionalProvider = ambulanceProviderRepository.findByAppUser_Id(id2);
        Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(id1);

        if (optionalProvider.isPresent() && optionalAmbulanceTrip.isPresent()) {
            AmbulanceProvider provider = optionalProvider.get();
            AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();

            ambulanceTrip.setAmbulanceProvider(provider.getAppUser());

            ambulanceTrip.setReviewChecked(0);

            provider.balance += ambulanceTrip.getPrice() - AppConstants.perUserCharge;
            ambulanceProviderRepository.save(provider);

            BkashCreateResponse bkashCreateResponse = bkashPaymentService
                    .createPayment(ambulanceTrip.getPrice().toString());

            ambulanceTrip.setPrice(ambulanceTrip.getPrice()-AppConstants.perUserCharge);

            AmbulanceTrip savedTrip = ambulanceTripRepository.save(ambulanceTrip);

            bkashCreateResponse.setProductId(savedTrip.getId());
            bkashCreateResponse.setType("AmbulanceTrip");
            savedTrip.setPaymentId(bkashCreateResponse.getPaymentId());

            return new ResponseEntity<>(bkashCreateResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Ambulance provider or trip not found"),
                    HttpStatus.NOT_FOUND);
        }
    }

}
