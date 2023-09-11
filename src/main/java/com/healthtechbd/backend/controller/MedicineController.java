package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.MedicineOrderViewDTO;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.MedicineOrderRepository;
import com.healthtechbd.backend.repo.MedicineRepository;
import com.healthtechbd.backend.repo.PharmacyRepository;
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
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/add/medicine")
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        if (medicine.getName() == null || medicine.getName().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine name is empty"), HttpStatus.BAD_REQUEST);
        }
        if (medicine.getCompany() == null || medicine.getCompany().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Company name is empty"), HttpStatus.BAD_REQUEST);
        }
        if (medicine.getPrice() == null || medicine.getPrice() <= 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Invalid or empty medicine price"),
                    HttpStatus.BAD_REQUEST);
        }
        if (medicine.getGeneric_name() == null || medicine.getGeneric_name().trim().length() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "Generic name is empty"), HttpStatus.BAD_REQUEST);
        }
        if (medicineRepository.existsByName(medicine.getName())
                && medicineRepository.existsByCompany(medicine.getCompany())) {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine already exists"), HttpStatus.BAD_REQUEST);
        }

        medicineRepository.save(medicine);

        return new ResponseEntity<>(ApiResponse.create("create", "Medicine successfully added/updated"), HttpStatus.OK);

    }

    @GetMapping("/medicine/order/all")
    public ResponseEntity<?> getAllMedicalOrders(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByAppUser_Id(user.getId());

        Pharmacy pharmacy = optionalPharmacy.get();

        List<MedicineOrder> medicineOrders = medicineOrderRepository.findAll();

        if (medicineOrders.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No medicine order in this place"), HttpStatus.OK);
        }

        List<MedicineOrderViewDTO> medicineOrderViewDTOS = new ArrayList<>();

        for (var medicineOrder : medicineOrders) {
            MedicineOrderViewDTO medicineOrderViewDTO = new MedicineOrderViewDTO();
            medicineOrderViewDTO.setId(medicineOrder.getId());
            medicineOrderViewDTO.setUserId(medicineOrder.getUser().getId());
            medicineOrderViewDTO.setContactNo(medicineOrder.getUser().getContactNo());
            medicineOrderViewDTO
                    .setUserName(medicineOrder.getUser().getFirstName() + " " + medicineOrder.getUser().getLastName());
            medicineOrderViewDTO.setDescription(medicineOrder.getDescription());
            medicineOrderViewDTO.setPrice(medicineOrder.getPrice());

            medicineOrderViewDTOS.add(medicineOrderViewDTO);
        }

        return new ResponseEntity<>(medicineOrderViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/medicine/order/update/{id}")
    public ResponseEntity<?> updateMedicineOrder(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.BAD_REQUEST);
        }

        Optional<MedicineOrder> optionalMedicineOrder = medicineOrderRepository.findById(id);

        if (!optionalMedicineOrder.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine Order not found"),
                    HttpStatus.BAD_REQUEST);
        }

        MedicineOrder medicineOrder = optionalMedicineOrder.get();

        medicineOrder.setPharmacy(appUser);

        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByAppUser_Id(appUser.getId());

        optionalPharmacy.get().balance += medicineOrder.getPrice();

        pharmacyRepository.save(optionalPharmacy.get());

        medicineOrderRepository.save(medicineOrder);

        return new ResponseEntity<>(ApiResponse.create("update", "medicine order updated"), HttpStatus.OK);
    }

    @GetMapping("/medicine/order/update/delivered/{id}")
    public ResponseEntity<?> updateMedicineOrdrerToDelivered(@PathVariable(name = "id") Long id) {
        Optional<MedicineOrder> optionalMedicineOrder = medicineOrderRepository.findById(id);

        if (!optionalMedicineOrder.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine Order not found"),
                    HttpStatus.BAD_REQUEST);
        }

        MedicineOrder medicineOrder = optionalMedicineOrder.get();

        medicineOrder.setDelivered(1);

        medicineOrderRepository.save(medicineOrder);

        return new ResponseEntity<>(ApiResponse.create("update", "medicine order delivered"), HttpStatus.OK);

    }

    @GetMapping("/medicine/all")
    public ResponseEntity<?> showAllMedicineDetails() {
        List<Medicine> medicines = medicineRepository.findAll();

        if (medicines.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "No medicine exists"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    @DeleteMapping("delete/medicine/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable(name = "id") Long id) {
        try {
            medicineRepository.deleteById(id);
            return new ResponseEntity<>(ApiResponse.create("delete", "Medicine is deleted"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine can not be deleted at this moment"),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
