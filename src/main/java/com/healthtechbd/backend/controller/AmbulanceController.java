package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AmbulanceDTO;
import com.healthtechbd.backend.entity.Ambulance;
import com.healthtechbd.backend.entity.AmbulanceProvider;
import com.healthtechbd.backend.entity.AmbulanceTrip;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.AmbulanceProviderRepository;
import com.healthtechbd.backend.repo.AmbulanceRepository;
import com.healthtechbd.backend.repo.AmbulanceTripRepository;
import com.healthtechbd.backend.repo.AppUserRepository;
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

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
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

        Optional<AmbulanceProvider> optionalAmbulanceProvider = ambulanceProviderRepository.findByAppUser_Id(appUser.getId());

        if (!optionalAmbulanceProvider.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Provider not found"), HttpStatus.BAD_REQUEST);
        }

        ambulance.setAmbulanceProvider(optionalAmbulanceProvider.get());

        ambulanceRepository.save(ambulance);

        return new ResponseEntity<>(ApiResponse.create("create", "Ambulance added"), HttpStatus.OK);
    }

    @GetMapping("/ambulance/{id}")
    public ResponseEntity<?> getAllAmbulances(@PathVariable(name = "id") Long id) {
        Optional<AppUser> opAppUser = userRepository.findById(id);

        List<Ambulance> ambulances = ambulanceRepository.findByAmbulanceProvider_Id(opAppUser.get().getId());

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

    @GetMapping("/delete/ambulance/{id}")
    public ResponseEntity<?>deleteDiagnosis(@PathVariable(name ="id")Long id)
    {
        try
        {
            ambulanceRepository.deleteById(id);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(ApiResponse.create("error", "Ambulance can't be deleted"),HttpStatus.BAD_REQUEST);
        }

        return  new ResponseEntity<>(ApiResponse.create("delete","Ambulance deleted"),HttpStatus.OK);
    }


    @PostMapping("/ambulance/trip/bid/{id}")
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

    @PostMapping("update/ambulancetrip/{id}")
    public ResponseEntity<?> updateAmbulanceTrip(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        AppUser ambulanceProvider = userService.returnUser(request);

        Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(id);

        if (!optionalAmbulanceTrip.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "trip not found"), HttpStatus.BAD_REQUEST);
        }

        AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();

        ambulanceTrip.setAmbulanceProvider(ambulanceProvider);

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("update", "trip updated"), HttpStatus.OK);

    }


}
