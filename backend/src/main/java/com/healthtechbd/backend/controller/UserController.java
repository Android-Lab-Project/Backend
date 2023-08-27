package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DiagnosisReportDTO;
import com.healthtechbd.backend.dto.DoctorPrescriptionDTO;
import com.healthtechbd.backend.dto.rpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.DiagnosisOrder;
import com.healthtechbd.backend.entity.DoctorSerial;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DiagnosisOrderRepository;
import com.healthtechbd.backend.repo.DoctorSerialRepository;
import com.healthtechbd.backend.repo.HospitalRepository;
import com.healthtechbd.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class UserController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard/reports&prescriptions")
    public ResponseEntity<?> getAllReportsPrescriptions(HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        List<DoctorSerial> doctorSerials = doctorSerialRepository.findByUser_Id(appUser.getId());

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository.findByUser_Id(appUser.getId());

        rpDTO rps = new rpDTO();

        rps.setAllPrescriptions(new ArrayList<>());
        rps.setAllReports(new ArrayList<>());

        for (int i = 0; i < doctorSerials.size(); i++) {
            DoctorPrescriptionDTO doctorPrescriptionDTO = new DoctorPrescriptionDTO();

            doctorPrescriptionDTO.setPrescription(doctorSerials.get(i).getPrescription());
            doctorPrescriptionDTO.setDoctorName(doctorSerials.get(i).getDoctor().getFirstName() + " " + doctorSerials.get(i).getDoctor().getLastName());
            doctorPrescriptionDTO.setDoctorId(doctorSerials.get(i).getDoctor().getId());

            rps.getAllPrescriptions().add(doctorPrescriptionDTO);
        }

        for (int i = 0; i < diagnosisOrders.size(); i++) {
            DiagnosisReportDTO diagnosisReportDTO = new DiagnosisReportDTO();

            diagnosisReportDTO.setDescription(diagnosisOrders.get(i).getDescription());
            diagnosisReportDTO.setReportURL(diagnosisOrders.get(i).getReportURL());
            diagnosisReportDTO.setHospitalId(diagnosisOrders.get(i).getHospital().getId());
            diagnosisReportDTO.setHospitalName(hospitalRepository.findByAppUser_Id(diagnosisOrders.get(i).getHospital().getId()).get().getHospitalName());

            rps.getAllReports().add(diagnosisReportDTO);
        }

        return new ResponseEntity<>(rps, HttpStatus.OK);
    }

}
