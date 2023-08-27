package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.*;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class UserController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private AmbulanceProviderRepository ambulanceProviderRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Autowired
    private AmbulanceTripRepository ambulanceTripRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

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

    @PostMapping("/order/diagnosis")
    public ResponseEntity<?> createDiagnosisOrder(@RequestBody DiagnosisOrderDTO diagnosisOrderDTO, HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        Optional<AppUser> opHospital = userRepository.findById(diagnosisOrderDTO.getHospitalId());

        AppUser hospital = new AppUser();

        if (!opHospital.isPresent()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital does not exist"), HttpStatus.BAD_REQUEST);
        }

        hospital = opHospital.get();

        DiagnosisOrder diagnosisOrder = modelMapper.map(diagnosisOrderDTO, DiagnosisOrder.class);

        diagnosisOrder.setUser(appUser);
        diagnosisOrder.setHospital(hospital);
        diagnosisOrder.setDate(LocalDate.now());
        diagnosisOrderRepository.save(diagnosisOrder);

        return new ResponseEntity<>(ApiResponse.create("create", "Diagnosis order created"), HttpStatus.OK);

    }

    @PostMapping("/add/doctorSerial")
    public ResponseEntity<?> createDoctorSerial(@RequestBody DoctorSerialDTO doctorSerialDTO, HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Optional<AppUser> opDoctor = userRepository.findById(doctorSerialDTO.getDoctor_id());

        DoctorSerial doctorSerial = modelMapper.map(doctorSerialDTO, DoctorSerial.class);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(opDoctor.get().getId());

        Doctor doctor = optionalDoctor.get();

        if(doctorSerialDTO.getType().equals("online"))
        {
            for(int i=0;i<doctor.getAvailableOnlineTimes().size();i++)
            {
                if(doctor.getAvailableOnlineTimes().get(i).getDate().equals(doctorSerialDTO.getDate()))
                {
                    doctor.getAvailableOnlineTimes().get(i).onlineCount++;
                }
            }
        }

        if(doctorSerialDTO.getType().equals("offline"))
        {
            for(int i=0;i<doctor.getAvailableTimes().size();i++)
            {
                if(doctor.getAvailableTimes().get(i).getDate().equals(doctorSerialDTO.getDate()))
                {
                    doctor.getAvailableTimes().get(i).count++;
                }
            }
        }

        doctorSerial.setUser(user);
        doctorSerial.setDoctor(opDoctor.get());

        doctorSerialRepository.save(doctorSerial);

        return new ResponseEntity<>(ApiResponse.create("create", "doctor Serial added"), HttpStatus.OK);
    }

    @PostMapping("/add/medicine/order")
    public ResponseEntity<?> createMedicineOrder(@RequestBody MedicineOrder medicineOrder, HttpServletRequest request) {
        AppUser user = userService.returnUser(request);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.BAD_REQUEST);
        }

        medicineOrder.setUser(user);
        medicineOrderRepository.save(medicineOrder);

        return new ResponseEntity<>(ApiResponse.create("create", "medicine order created"), HttpStatus.OK);
    }

    @PostMapping("/add/ambulance/trip")
    public ResponseEntity<?>createAmbulanceTrip(@RequestBody AmbulanceTrip ambulanceTrip, HttpServletRequest request)
    {
        AppUser user = userService.returnUser(request);

        ambulanceTrip.setUser(user);

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("create","Trip created"),HttpStatus.OK);
    }

    @PostMapping("/add/review/{id2}")
    public ResponseEntity<?> saveReview(@RequestParam(name = "review") String reviewStr,@RequestParam(name = "star") Long starCount, @PathVariable(name = "id2") Long id2, HttpServletRequest request) {

        AppUser reviewer = userService.returnUser(request);
        Optional<AppUser> optionalSubject = userRepository.findById(id2);

        AppUser subject = new AppUser();

        if(reviewer==null)
        {
            return new ResponseEntity<>(ApiResponse.create("error", "Reviewer not found"), HttpStatus.BAD_REQUEST);
        }
        if (optionalSubject.isPresent()) {
            subject = optionalSubject.get();
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Subject not found"), HttpStatus.BAD_REQUEST);
        }

        Review review = new Review();
        review.setReview(reviewStr);
        review.setStarCount(starCount);
        review.setReviewer(reviewer);
        review.setSubject(subject);

        reviewRepository.save(review);
        ApiResponse createResponse = ApiResponse.create("create", "Review Saved");
        return new ResponseEntity<>(createResponse, HttpStatus.OK);

    }

    @GetMapping("/profile")
    public ResponseEntity<?>getProfileDetails(HttpServletRequest request)
    {
        AppUser appUser = userService.returnUser(request);
        Object userdetails =null;
        if(appUser.getRoles().get(0).getRoleType().equals("USER"))
        {
            appUser.setPassword(null);

            userdetails = appUser;
        }
        else if(appUser.getRoles().get(0).getRoleType().equals("DOCTOR"))
        {
            Optional<Doctor> optional = doctorRepository.findByAppUser_Id(appUser.getId());

            optional.get().getAppUser().setPassword(null);

            userdetails = optional.get();
        }
        else if(appUser.getRoles().get(0).getRoleType().equals("HOSPITAL"))
        {
            Optional<Hospital> optional = hospitalRepository.findByAppUser_Id(appUser.getId());

            optional.get().getAppUser().setPassword(null);

            userdetails = optional.get();
        }
        else if(appUser.getRoles().get(0).getRoleType().equals("PHARMACY"))
        {
            Optional<Pharmacy> optional = pharmacyRepository.findByAppUser_Id(appUser.getId());

            optional.get().getAppUser().setPassword(null);

            userdetails = optional.get();
        }
        else if(appUser.getRoles().get(0).getRoleType().equals("AMBULANCE"))
        {
            Optional<AmbulanceProvider> optional = ambulanceProviderRepository.findByAppUser_Id(appUser.getId());

            optional.get().getAppUser().setPassword(null);

            userdetails = optional.get();
        }

        if(userdetails==null)
        {
            return new ResponseEntity<>(ApiResponse.create("error","Profile not found"),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userdetails,HttpStatus.OK);
    }



}


