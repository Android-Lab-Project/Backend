package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.*;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.BkashPaymentService;
import com.healthtechbd.backend.service.TimeService;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.AppConstants;
import com.healthtechbd.backend.utils.BkashCreateResponse;
import com.healthtechbd.backend.utils.BkashExecuteResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class AppUserController {

    @Autowired
    private AdminRepository adminRepository;
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
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private AmbulanceTripRepository ambulanceTripRepository;

    @Autowired
    private UserResponseRepository userResponseRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private BkashPaymentService bkashPaymentService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/dashboard/reports&prescriptions")
    public ResponseEntity<?> getAllReportsPrescriptions(HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        List<DoctorSerial> doctorSerials = doctorSerialRepository.findByUser_IdAndPrescriptionIsNotNull(appUser.getId());

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository.findByUser_IdAndReportURLIsNotNull(appUser.getId());

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

    @GetMapping("bkash/execute/payment")
    public ResponseEntity<?> executePayment(@RequestParam(name = "paymentId") String paymentId, @RequestParam(name = "status") String status, @RequestParam(name = "type") String type, @RequestParam(name = "productId") Long prouductId) {
        if (status.equalsIgnoreCase("success")) {
            BkashExecuteResponse bkashExecuteResponse = bkashPaymentService.executePayment(paymentId);

            if (type.equalsIgnoreCase("DiagnosisOrder")) {
                Optional<DiagnosisOrder> optionalDiagnosisOrder = diagnosisOrderRepository.findById(prouductId);

                optionalDiagnosisOrder.get().setTrxId(bkashExecuteResponse.getTrxID());
                diagnosisOrderRepository.save(optionalDiagnosisOrder.get());

                AppUser hospitalUser = optionalDiagnosisOrder.get().getHospital();

                Optional<Hospital> hospital = hospitalRepository.findByAppUser_Id(hospitalUser.getId());

                hospital.get().balance += optionalDiagnosisOrder.get().getPrice();

                hospitalRepository.save(hospital.get());

            } else if (type.equalsIgnoreCase("DoctorSerial")) {
                Optional<DoctorSerial> optionalDoctorSerial = doctorSerialRepository.findById(prouductId);

                optionalDoctorSerial.get().setTrxId(bkashExecuteResponse.getTrxID());

                doctorSerialRepository.save(optionalDoctorSerial.get());

                AppUser doctorUser = optionalDoctorSerial.get().getDoctor();

                Optional<Doctor> doctor = doctorRepository.findByAppUser_Id(doctorUser.getId());

                doctor.get().balance += optionalDoctorSerial.get().getPrice();

                doctorRepository.save(doctor.get());
            } else if (type.equalsIgnoreCase("MedicineOrder")) {
                Optional<MedicineOrder> optionalMedicineOrder = medicineOrderRepository.findById(prouductId);

                optionalMedicineOrder.get().setTrxId(bkashExecuteResponse.getTrxID());

                medicineOrderRepository.save(optionalMedicineOrder.get());
            } else if (type.equalsIgnoreCase("AmbulanceTrip")) {
                Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(prouductId);

                optionalAmbulanceTrip.get().setTrxId(bkashExecuteResponse.getTrxID());

                ambulanceTripRepository.save(optionalAmbulanceTrip.get());
            }

            return new ResponseEntity<>(bkashExecuteResponse, HttpStatus.OK);
        } else {
            if (type.equalsIgnoreCase("DiagnosisOrder")) {
                diagnosisOrderRepository.deleteById(prouductId);
            } else if (type.equalsIgnoreCase("DoctorSerial")) {
                doctorSerialRepository.deleteById(prouductId);
            } else if (type.equalsIgnoreCase("MedicineOrder")) {
                medicineOrderRepository.deleteById(prouductId);
            } else if (type.equalsIgnoreCase("AmbulanceTrip")) {
                ambulanceTripRepository.deleteById(prouductId);
            }

            return new ResponseEntity<>(ApiResponse.create("error", "Product is canceled or not successful"), HttpStatus.OK);
        }
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

        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(opHospital.get().getId());

        DiagnosisOrder diagnosisOrder = modelMapper.map(diagnosisOrderDTO, DiagnosisOrder.class);

        diagnosisOrder.setUser(appUser);
        diagnosisOrder.setHospital(hospital);
        diagnosisOrder.setDate(LocalDate.now());

        optionalHospital.get().balance += diagnosisOrder.getPrice() - AppConstants.perUserCharge;

        hospitalRepository.save(optionalHospital.get());

        BkashCreateResponse bkashCreateResponse = bkashPaymentService.createPayment(diagnosisOrder.getPrice().toString());

        bkashCreateResponse.setType("DiagnosisOrder");

        DiagnosisOrder savedDiagnosisOrder = diagnosisOrderRepository.save(diagnosisOrder);

        bkashCreateResponse.setProductId(savedDiagnosisOrder.getId());

        savedDiagnosisOrder.setPaymentId(bkashCreateResponse.getPaymentId());

        diagnosisOrderRepository.save(savedDiagnosisOrder);

        return new ResponseEntity<>(bkashCreateResponse, HttpStatus.OK);

    }

    @PostMapping("/add/doctorSerial")
    public ResponseEntity<?> createDoctorSerial(@RequestBody DoctorSerialDTO doctorSerialDTO, HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Optional<AppUser> opDoctor = userRepository.findById(doctorSerialDTO.getDoctor_id());

        DoctorSerial doctorSerial = modelMapper.map(doctorSerialDTO, DoctorSerial.class);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(opDoctor.get().getId());

        Doctor doctor = optionalDoctor.get();

        if (doctorSerialDTO.getType().equalsIgnoreCase("online")) {
            for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {
                if (doctor.getAvailableOnlineTimes().get(i).getDate().equals(doctorSerialDTO.getAppointmentDate())) {
                    doctor.getAvailableOnlineTimes().get(i).count++;
                }
            }
        }

        if (doctorSerialDTO.getType().equalsIgnoreCase("offline")) {
            for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {
                if (doctor.getAvailableTimes().get(i).getDate().equals(doctorSerialDTO.getAppointmentDate())) {
                    doctor.getAvailableTimes().get(i).count++;
                }
            }
        }

        doctor.balance += doctorSerial.getPrice() - AppConstants.perUserCharge;

        doctorRepository.save(doctor);

        doctorSerial.setDate(LocalDate.now());

        doctorSerial.setUser(user);
        doctorSerial.setDoctor(opDoctor.get());


        BkashCreateResponse bkashCreateResponse = bkashPaymentService.createPayment(doctorSerial.getPrice().toString());

        bkashCreateResponse.setType("DoctorSerial");

        DoctorSerial savedDoctorSerial = doctorSerialRepository.save(doctorSerial);

        bkashCreateResponse.setProductId(savedDoctorSerial.getId());

        savedDoctorSerial.setPaymentId(bkashCreateResponse.getPaymentId());

        doctorSerialRepository.save(savedDoctorSerial);

        return new ResponseEntity<>(bkashCreateResponse, HttpStatus.OK);
    }

    @PostMapping("/add/medicine/order")
    public ResponseEntity<?> createMedicineOrder(@RequestBody MedicineOrder medicineOrder, HttpServletRequest request) {
        AppUser user = userService.returnUser(request);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.BAD_REQUEST);
        }

        medicineOrder.setUser(user);
        medicineOrder.setDate(LocalDate.now());
        medicineOrder.setDelivered(0);


        BkashCreateResponse bkashCreateResponse = bkashPaymentService.createPayment(medicineOrder.getPrice().toString());

        MedicineOrder savedMedicineOrder = medicineOrderRepository.save(medicineOrder);

        bkashCreateResponse.setType("MedicineOrder");
        bkashCreateResponse.setProductId(savedMedicineOrder.getId());
        savedMedicineOrder.setPaymentId(bkashCreateResponse.getPaymentId());

        medicineOrderRepository.save(savedMedicineOrder);

        return new ResponseEntity<>(bkashCreateResponse, HttpStatus.OK);
    }

    @PostMapping("/add/ambulance/trip")
    public ResponseEntity<?> createAmbulanceTrip(@RequestBody AmbulanceTrip ambulanceTrip, HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        ambulanceTrip.setUser(user);

        ambulanceTrip.setBidders(new ArrayList<>());

        ambulanceTrip.setDate(LocalDate.now());

        BkashCreateResponse bkashCreateResponse = bkashPaymentService.createPayment(AppConstants.perUserCharge.toString());

        AmbulanceTrip savedTrip = ambulanceTripRepository.save(ambulanceTrip);

        bkashCreateResponse.setProductId(savedTrip.getId());
        bkashCreateResponse.setType("AmbulanceTrip");
        savedTrip.setPaymentId(bkashCreateResponse.getPaymentId());
        ambulanceTripRepository.save(savedTrip);

        return new ResponseEntity<>(ApiResponse.create("create", "Trip created"), HttpStatus.OK);
    }

    @PostMapping("/add/review/{id}")
    public ResponseEntity<?> saveReview(@RequestParam(name = "review") String reviewStr, @RequestParam(name = "star") Long starCount, @PathVariable(name = "id") Long id, HttpServletRequest request) {

        AppUser reviewer = userService.returnUser(request);
        Optional<AppUser> optionalSubject = userRepository.findById(id);

        AppUser subject = new AppUser();

        if (reviewer == null) {
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

    @PostMapping("/add/response")
    public ResponseEntity<?> addUserResponse(HttpServletRequest request, @RequestParam String message) {
        AppUser user = userService.returnUser(request);

        UserResponse userResponse = new UserResponse();
        userResponse.setDate(LocalDate.now());
        userResponse.setEmail(user.getEmail());
        userResponse.setMessage(message);
        userResponse.setChecked(0);

        userResponseRepository.save(userResponse);

        return new ResponseEntity<>(ApiResponse.create("create", "Response created"), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfileDetails(HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);
        Object userdetails = null;
        if (appUser.getRoles().get(0).getRoleType().equals("USER")) {
            UserDTO userDTO = modelMapper.map(appUser, UserDTO.class);

            userdetails = userDTO;
        } else if (appUser.getRoles().get(0).getRoleType().equals("ADMIN")) {
            UserDTO userDTO = modelMapper.map(appUser, UserDTO.class);
            AdminDTO adminDTO = modelMapper.map(userDTO, AdminDTO.class);
            adminDTO.setBalance(adminRepository.findById(1L).get().getBalance());

            userdetails = adminDTO;
        } else if (appUser.getRoles().get(0).getRoleType().equals("HOSPITAL")) {
            Optional<Hospital> optional = hospitalRepository.findByAppUser_Id(appUser.getId());

            HospitalDTO hospitalDTO = modelMapper.map(optional.get(), HospitalDTO.class);
            hospitalDTO.setId(appUser.getId());
            hospitalDTO.setFirstName(appUser.getFirstName());
            hospitalDTO.setLastName(appUser.getLastName());
            hospitalDTO.setEmail(appUser.getEmail());
            hospitalDTO.setContactNo(appUser.getContactNo());
            hospitalDTO.setDp(appUser.getDp());
            hospitalDTO.setRating(reviewRepository.findAvgRating(appUser.getId()));
            hospitalDTO.setReviewCount(reviewRepository.findCount(appUser.getId()));

            userdetails = hospitalDTO;
        } else if (appUser.getRoles().get(0).getRoleType().equals("PHARMACY")) {
            Optional<Pharmacy> optional = pharmacyRepository.findByAppUser_Id(appUser.getId());

            PharmacyDTO pharmacyDTO = modelMapper.map(optional.get(), PharmacyDTO.class);

            pharmacyDTO.setId(appUser.getId());
            pharmacyDTO.setFirstName(appUser.getFirstName());
            pharmacyDTO.setLastName(appUser.getLastName());
            pharmacyDTO.setEmail(appUser.getEmail());
            pharmacyDTO.setContactNo(appUser.getContactNo());
            pharmacyDTO.setDp(appUser.getDp());
            pharmacyDTO.setRating(reviewRepository.findAvgRating(appUser.getId()));
            pharmacyDTO.setReviewCount(reviewRepository.findCount(appUser.getId()));

            userdetails = pharmacyDTO;
        } else if (appUser.getRoles().get(0).getRoleType().equals("AMBULANCE")) {
            Optional<AmbulanceProvider> optional = ambulanceProviderRepository.findByAppUser_Id(appUser.getId());


            AmbulanceProviderDTO ambulanceProviderDTO = modelMapper.map(optional.get(), AmbulanceProviderDTO.class);

            ambulanceProviderDTO.setId(appUser.getId());
            ambulanceProviderDTO.setFirstName(appUser.getFirstName());
            ambulanceProviderDTO.setLastName(appUser.getLastName());
            ambulanceProviderDTO.setEmail(appUser.getEmail());
            ambulanceProviderDTO.setContactNo(appUser.getContactNo());
            ambulanceProviderDTO.setDp(appUser.getDp());
            ambulanceProviderDTO.setRating(reviewRepository.findAvgRating(appUser.getId()));
            ambulanceProviderDTO.setReviewCount(reviewRepository.findCount(appUser.getId()));


            userdetails = ambulanceProviderDTO;
        }

        if (userdetails == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Profile not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userdetails, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "user not found"), HttpStatus.OK);
        }

        StatisticsDTO statisticsDTO = new StatisticsDTO();

        LocalDate now = LocalDate.now();
        LocalDate sevenDaysAgo = now.minusDays(7);
        LocalDate thirtyDaysAgo = now.minusDays(30);

        String roleType = user.getRoles().get(0).getRoleType();

        if ("DOCTOR".equalsIgnoreCase(roleType)) {
            // Doctor statistics logic
            statisticsDTO.set_7DaysCount(doctorSerialRepository.countSerialsByDoctorAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(doctorSerialRepository.countSerialsByDoctorAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(doctorSerialRepository.countSerialsByDoctor(user.getId()));
            statisticsDTO.set_7DaysIncome(doctorSerialRepository.sumPriceByDoctorAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(doctorSerialRepository.sumPriceByDoctorAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(doctorSerialRepository.sumPriceByDoctor(user.getId()));

            List<Object[]> incomeList = doctorSerialRepository.sumPriceByDoctorAndDateGroupByDate(user.getId(), thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else if ("PHARMACY".equalsIgnoreCase(roleType)) {
            // Pharmacy statistics logic
            statisticsDTO.set_7DaysCount(medicineOrderRepository.countMedicineOrdersByPharmacyAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(medicineOrderRepository.countMedicineOrdersByPharmacyAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(medicineOrderRepository.countMedicineOrdersByPharmacy(user.getId()));
            statisticsDTO.set_7DaysIncome(medicineOrderRepository.sumPriceByPharmacyAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(medicineOrderRepository.sumPriceByPharmacyAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(medicineOrderRepository.sumPriceByPharmacy(user.getId()));

            List<Object[]> incomeList = medicineOrderRepository.sumPriceByPharmacyAndDateGroupByDate(user.getId(), thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else if ("HOSPITAL".equalsIgnoreCase(roleType)) {
            // Hospital statistics logic
            statisticsDTO.set_7DaysCount(diagnosisOrderRepository.countDiagnosisOrdersByHospitalAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(diagnosisOrderRepository.countDiagnosisOrdersByHospitalAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(diagnosisOrderRepository.countDiagnosisOrdersByHospital(user.getId()));
            statisticsDTO.set_7DaysIncome(diagnosisOrderRepository.sumPriceByHospitalAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(diagnosisOrderRepository.sumPriceByHospitalAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(diagnosisOrderRepository.sumPriceByHospital(user.getId()));

            List<Object[]> incomeList = diagnosisOrderRepository.sumPriceByHospitalAndDateGroupByDate(user.getId(), thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else if ("AMBULANCE".equalsIgnoreCase(roleType)) {
            // Ambulance statistics logic
            statisticsDTO.set_7DaysCount(ambulanceTripRepository.countTripsByAmbulanceProviderAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(ambulanceTripRepository.countTripsByAmbulanceProviderAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(ambulanceTripRepository.countTripsByAmbulanceProvider(user.getId()));
            statisticsDTO.set_7DaysIncome(ambulanceTripRepository.sumPriceByAmbulanceProviderAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(ambulanceTripRepository.sumPriceByAmbulanceProviderAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(ambulanceTripRepository.sumPriceByAmbulanceProvider(user.getId()));

            List<Object[]> incomeList = ambulanceTripRepository.sumPriceByAmbulanceProviderAndDateGroupByDate(user.getId(), thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "invalid role type"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
    }


    @GetMapping("/dashboard/user/upcoming/doctorserial")
    public ResponseEntity<?> getAllUpcomingForUser(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now());

        List<DoctorSerial> upcomingDoctorSerials = doctorSerialRepository.findByDateAndTimeAndUserId(LocalDate.now(), time, user.getId());

        if (upcomingDoctorSerials.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<UserDoctorSerialViewDTO> userDoctorSerialDTOs = new ArrayList<>();

        for (var doctorSerial : upcomingDoctorSerials) {
            UserDoctorSerialViewDTO userDoctorSerialDTO = new UserDoctorSerialViewDTO();
            userDoctorSerialDTO.setId(doctorSerial.getId());
            userDoctorSerialDTO.setTime(doctorSerial.getTime());
            userDoctorSerialDTO.setDoctorName(doctorSerial.getDoctor().getFirstName() + " " + doctorSerial.getDoctor().getLastName());

            userDoctorSerialDTOs.add(userDoctorSerialDTO);
        }

        return new ResponseEntity<>(userDoctorSerialDTOs, HttpStatus.OK);
    }

    @GetMapping("/dashboard/user/upcoming/diagnosis")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now());

        List<DiagnosisOrder> upcomingDiagnosisOrders = diagnosisOrderRepository.findByDateAndTimeAndUserId(LocalDate.now(), time, user.getId());

        if (upcomingDiagnosisOrders.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<UserDiagnosisOrderViewDTO> userDiagnosisOrderViewDTOS = new ArrayList<>();

        for (var diagnosisOrder : upcomingDiagnosisOrders) {
            UserDiagnosisOrderViewDTO userDiagnosisOrderViewDTO = new UserDiagnosisOrderViewDTO();
            userDiagnosisOrderViewDTO.setId(diagnosisOrder.getId());
            userDiagnosisOrderViewDTO.setDescription(diagnosisOrder.getDescription());
            userDiagnosisOrderViewDTO.setTime(diagnosisOrder.getTime());
            userDiagnosisOrderViewDTO.setPlace(diagnosisOrder.getPlace());
            userDiagnosisOrderViewDTO.setHospitalName(hospitalRepository.findByAppUser_Id(diagnosisOrder.getHospital().getId()).get().getHospitalName());

            userDiagnosisOrderViewDTOS.add(userDiagnosisOrderViewDTO);
        }

        return new ResponseEntity<>(userDiagnosisOrderViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/medicineorder/undelivered")
    public ResponseEntity<?> getAllUndelivered(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "user not found"), HttpStatus.BAD_REQUEST);
        }

        String roleType = user.getRoles().get(0).getRoleType();

        List<MedicineOrder> medicineOrders = new ArrayList<>();

        if (roleType.equalsIgnoreCase("USER")) {
            medicineOrders = medicineOrderRepository.findUndeliveredOrdersByUser(user.getId());
        } else if (roleType.equalsIgnoreCase("PHARMACY")) {
            medicineOrders = medicineOrderRepository.findUndeliveredOrdersByPharmacy(user.getId());
        }

        if (medicineOrders.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "Undelivered not found"), HttpStatus.OK);
        }

        List<MedicineOrderViewDTO> medicineOrderViewDTOS = new ArrayList<>();

        for (var medicineOrder : medicineOrders) {
            MedicineOrderViewDTO medicineOrderViewDTO = new MedicineOrderViewDTO();
            medicineOrderViewDTO.setId(medicineOrder.getId());
            medicineOrderViewDTO.setUserId(medicineOrder.getUser().getId());
            medicineOrderViewDTO.setPharmacyId(medicineOrder.getPharmacy().getId());
            medicineOrderViewDTO.setUserName(medicineOrder.getUser().getFirstName() + " " + medicineOrder.getUser().getLastName());
            medicineOrderViewDTO.setPharmacyName(medicineOrder.getPharmacy().getFirstName() + " " + medicineOrder.getPharmacy().getLastName());
            medicineOrderViewDTO.setDescription(medicineOrder.getDescription());
            medicineOrderViewDTO.setPrice(medicineOrder.getPrice());

            medicineOrderViewDTOS.add(medicineOrderViewDTO);
        }

        return new ResponseEntity<>(medicineOrderViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/ambulance/trip/upcoming")
    public ResponseEntity<?> getAllUpcomingTrip(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "user not found"), HttpStatus.BAD_REQUEST);
        }

        String roleType = user.getRoles().get(0).getRoleType();

        List<AmbulanceTrip> ambulanceTrips = new ArrayList<>();

        if (roleType.equalsIgnoreCase("USER")) {
            ambulanceTrips = ambulanceTripRepository.findUpcomingTripsByUser(LocalDate.now(), user.getId());
        } else if (roleType.equalsIgnoreCase("AMBULANCE")) {
            ambulanceTrips = ambulanceTripRepository.findUpcomingTripsByProvider(LocalDate.now(), user.getId());
        }

        if (ambulanceTrips.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming trip found"), HttpStatus.OK);
        }

        List<AmbulanceTripViewDTO> ambulanceTripViewDTOS = new ArrayList<>();

        for (var ambulanceTrip : ambulanceTrips) {
            AmbulanceTripViewDTO ambulanceTripViewDTO = new AmbulanceTripViewDTO();
            ambulanceTripViewDTO.setId(ambulanceTrip.getId());
            ambulanceTripViewDTO.setUserId(ambulanceTrip.getUser().getId());
            ambulanceTripViewDTO.setProviderId(ambulanceTrip.getAmbulanceProvider().getId());
            ambulanceTripViewDTO.setUserName(ambulanceTrip.getUser().getFirstName() + " " + ambulanceTrip.getUser().getLastName());
            ambulanceTripViewDTO.setProviderName(ambulanceTrip.getAmbulanceProvider().getFirstName() + " " + ambulanceTrip.getAmbulanceProvider().getLastName());
            ambulanceTripViewDTO.setSource(ambulanceTrip.getSource());
            ambulanceTripViewDTO.setDestination(ambulanceTrip.getDestination());
            ambulanceTripViewDTO.setPrice(ambulanceTrip.getPrice());
            ambulanceTripViewDTO.setDate(ambulanceTrip.getDate());

            ambulanceTripViewDTOS.add(ambulanceTripViewDTO);
        }

        return new ResponseEntity<>(ambulanceTripViewDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {

        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
        }

        Long id = appUser.getId();

        List<Role> roles = appUser.getRoles();


        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getRoleType().equals("USER")) {
                userRepository.delete(appUser);
            }
            if (roles.get(i).getRoleType().equals("DOCTOR")) {

                Optional<Doctor> doctor = doctorRepository.findByAppUser_Id(id);

                if (!doctor.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }
                doctorRepository.delete(doctor.get());
            }
            if (roles.get(i).getRoleType().equals("HOSPITAL")) {
                Optional<Hospital> hospital = hospitalRepository.findByAppUser_Id(id);

                if (!hospital.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }

                hospitalRepository.delete(hospital.get());
            }
            if (roles.get(i).getRoleType().equals("PHARMACY")) {
                Optional<Pharmacy> pharmacy = pharmacyRepository.findByAppUser_Id(id);

                if (!pharmacy.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }

                pharmacyRepository.delete(pharmacy.get());
            }
            if (roles.get(i).getRoleType().equals("AMBULANCE")) {
                Optional<AmbulanceProvider> ambulanceProvider = ambulanceProviderRepository.findByAppUser_Id(id);

                if (!ambulanceProvider.isPresent()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.BAD_REQUEST);
                }

                ambulanceProviderRepository.delete(ambulanceProvider.get());
            }
        }

        return new ResponseEntity<>(ApiResponse.create("delete", "User is deleted"), HttpStatus.OK);
    }

}



