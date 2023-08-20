package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.entity.Medicine;
import com.healthtechbd.backend.repo.MedicineRepository;
import com.healthtechbd.backend.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;
    @GetMapping("/medicine/all")
    public ResponseEntity<?> showAllMedicineDetails()
    {
        List<Medicine> medicines = medicineRepository.findAll();

        if(medicines.size()==0)
        {
            return new ResponseEntity<>(ApiResponse.create("error","No medicine exists"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(medicines,HttpStatus.OK);
    }

    @DeleteMapping("delete/medicine/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable(name="id")Long id)
    {
        try{
           medicineRepository.deleteById(id);
           return new ResponseEntity<>(ApiResponse.create("delete","Medicine is deleted"),HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(ApiResponse.create("error","Medicine can not be deleted at this moment"),HttpStatus.BAD_REQUEST);
        }
    }



}
