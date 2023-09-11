package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceDTO;
import com.healthtechbd.backend.dto.AmbulanceTripViewDTO;
import com.healthtechbd.backend.entity.Ambulance;
import com.healthtechbd.backend.entity.AmbulanceProvider;
import com.healthtechbd.backend.entity.AmbulanceTrip;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RestController
public class AmbulanceController {

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

        if (!optionalAmbulanceProvider.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Provider not found"), HttpStatus.BAD_REQUEST);
        }

        ambulance.setAmbulanceProvider(optionalAmbulanceProvider.get());

        ambulanceRepository.save(ambulance);

        return new ResponseEntity<>(ApiResponse.create("create", "Ambulance added"), HttpStatus.OK);
    }

    @GetMapping("/ambulance/{id}")
    public ResponseEntity<?> getAllAmbulances(@PathVariable(name = "id") Long id) {

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository.findByAppUser_Id(id);

        List<Ambulance> ambulances = ambulanceRepository
                .findByAmbulanceProvider_Id(optionalAmbulanceProvider.get().getId());

        if (ambulances.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Ambulance Found"), HttpStatus.OK);
        }

        List<AmbulanceDTO> ambulanceDTOS = new ArrayList<>();

        for (var i : ambulances) {
            AmbulanceDTO ambulanceDTO = modelMapper.map(i, AmbulanceDTO.class);
            ambulanceDTOS.add(ambulanceDTO);
        }

        return new ResponseEntity<>(ambulanceDTOS, HttpStatus.OK);

    }

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

    @GetMapping("/ambulance/trip/all")
    public ResponseEntity<?> getAllTrips() {
        List<AmbulanceTrip> ambulanceTrips = ambulanceTripRepository.findAll();
        if (ambulanceTrips.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No Trip found"), HttpStatus.OK);
        }
        List<AmbulanceTripViewDTO> ambulanceTripViewDTOS = new ArrayList<>();

        for (var ambulanceTrip : ambulanceTrips) {
            AmbulanceTripViewDTO ambulanceTripViewDTO = new AmbulanceTripViewDTO();
            ambulanceTripViewDTO.setId(ambulanceTrip.getId());
            ambulanceTripViewDTO.setUserId(ambulanceTrip.getUser().getId());
            ambulanceTripViewDTO
                    .setUserName(ambulanceTrip.getUser().getFirstName() + " " + ambulanceTrip.getUser().getLastName());
            ambulanceTripViewDTO.setSource(ambulanceTrip.getSource());
            ambulanceTripViewDTO.setDestination(ambulanceTrip.getDestination());
            ambulanceTripViewDTO.setPrice(ambulanceTrip.getPrice());
            ambulanceTripViewDTO.setOrderDate(ambulanceTrip.getOrderDate());

            ambulanceTripViewDTOS.add(ambulanceTripViewDTO);
        }

        return new ResponseEntity<>(ambulanceTripViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/ambulance/trip/bid/{id}")
    public ResponseEntity<?> addBidderToTrip(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        AppUser bidder = userService.returnUser(request);

        Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(id);

        if (!optionalAmbulanceTrip.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "trip not found"), HttpStatus.BAD_REQUEST);
        }

        AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();

        ambulanceTrip.getBidders().add(bidder);

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("update", "trip updated"), HttpStatus.OK);
    }

    @GetMapping("update/ambulancetrip/{id1}/{id2}")
    public ResponseEntity<?> updateAmbulanceTrip(HttpServletRequest request, @PathVariable(name = "id1") Long id1,
            @PathVariable(name = "id2") Long id2) {
        Optional<AmbulanceProvider> optionalProvider = ambulanceProviderRepository.findByAppUser_Id(id2);

        Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(id1);

        if (!optionalAmbulanceTrip.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "trip not found"), HttpStatus.BAD_REQUEST);
        }

        AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();

        ambulanceTrip.setAmbulanceProvider(optionalProvider.get().getAppUser());

        Long reviewCount = reviewRepository.countByUser(ambulanceTrip.getUser().getId(),
                ambulanceTrip.getAmbulanceProvider().getId());

        if (reviewCount >= 1) {
            ambulanceTrip.setReviewChecked(1);
        } else {
            ambulanceTrip.setReviewChecked(0);
        }

        optionalProvider.get().balance += ambulanceTrip.getPrice();

        ambulanceProviderRepository.save(optionalProvider.get());

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("update", "trip updated"), HttpStatus.OK);

    }
}
