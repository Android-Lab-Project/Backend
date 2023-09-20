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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('ADMIN')")
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

        return new ResponseEntity<>(ApiResponse.create("create", "Medicine successfully added"), HttpStatus.OK);

    }

    @PreAuthorize("hasAnyAuthority('PHARMACY','USER','ADMIN')")
    @GetMapping("/medicine/all")
    public ResponseEntity<?> showAllMedicineDetails() {
        List<Medicine> medicines = medicineRepository.findAll();

        if (medicines.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "No medicine exists"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/update/medicine/{id}")
    public ResponseEntity<?>updateMedicine(@PathVariable(name="id")Long id, @RequestParam(name="new_price")Long new_price)
    {
        Optional<Medicine>optionalMedicine = medicineRepository.findById(id);

        if(optionalMedicine.isEmpty())
        {
            return new ResponseEntity<>(ApiResponse.create("error","Medicine not found"),HttpStatus.NOT_FOUND);
        }

        Medicine medicine =optionalMedicine.get();

        medicine.setPrice(new_price*1.0);

        medicineRepository.save(medicine);

        return new ResponseEntity<>(ApiResponse.create("update", "Medicine Price updated"),HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('PHARMACY','USER')")
    @GetMapping("/medicine/order/all")
    public ResponseEntity<?> getAllMedicalOrders() {

        List<MedicineOrder> medicineOrders = medicineOrderRepository.findByPharmacyIsNull();

        if (medicineOrders.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No medicine order found at this moment"), HttpStatus.OK);
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
            medicineOrderViewDTO.setPlace(medicineOrder.getPlace());
            medicineOrderViewDTO.setPrice(medicineOrder.getPrice());

            medicineOrderViewDTOS.add(medicineOrderViewDTO);
        }

        return new ResponseEntity<>(medicineOrderViewDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PHARMACY','USER')")
    @GetMapping("/medicine/order/update/{id}")
    public ResponseEntity<?> updateMedicineOrder(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Pharmacy not found"), HttpStatus.NOT_FOUND);
        }

        Optional<MedicineOrder> optionalMedicineOrder = medicineOrderRepository.findById(id);

        if (optionalMedicineOrder.isPresent()) {
            MedicineOrder medicineOrder = optionalMedicineOrder.get();

            medicineOrder.setPharmacy(appUser);

            Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByAppUser_Id(appUser.getId());

            if (optionalPharmacy.isPresent()) {
                Pharmacy pharmacy = optionalPharmacy.get();
                pharmacy.balance += medicineOrder.getPrice();
                pharmacyRepository.save(pharmacy);

                medicineOrderRepository.save(medicineOrder);

                return new ResponseEntity<>(ApiResponse.create("update", "Medicine order updated"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ApiResponse.create("error", "Pharmacy not found"), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine Order not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('PHARMACY','USER')")
    @GetMapping("/medicine/order/update/delivered/{id}")
    public ResponseEntity<?> updateMedicineOrderToDelivered(@PathVariable(name = "id") Long id) {
        Optional<MedicineOrder> optionalMedicineOrder = medicineOrderRepository.findById(id);

        if (optionalMedicineOrder.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Medicine Order not found"),
                    HttpStatus.BAD_REQUEST);
        }

        MedicineOrder medicineOrder = optionalMedicineOrder.get();

        medicineOrder.setDelivered(1);

        medicineOrderRepository.save(medicineOrder);

        return new ResponseEntity<>(ApiResponse.create("update", "medicine order delivered"), HttpStatus.OK);

    }

}
