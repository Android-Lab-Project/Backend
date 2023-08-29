package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.MedicineOrderRepository;
import com.healthtechbd.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class PharmacyController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Autowired
    private UserService userService;


}
