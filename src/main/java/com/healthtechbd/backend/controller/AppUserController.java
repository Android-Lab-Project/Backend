package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.*;
import com.healthtechbd.backend.entity.*;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.service.*;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.AppConstants;
import com.healthtechbd.backend.utils.BkashCreateResponse;
import com.healthtechbd.backend.utils.BkashExecuteResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "Unused"})
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
@PreAuthorize("hasAuthority('USER')")
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
    private AppUserNotificationRepository appUserNotificationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BkashPaymentService bkashPaymentService;

    @Autowired
    private ModelMapper modelMapper;


    @PreAuthorize("hasAnyAuthority('HOSPITAL','AMBULANCE','PHARMACY','USER','ADMIN')")
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

            if (hospitalDTO.getRating() == null) {
                hospitalDTO.setRating(0.0);
            }

            if (hospitalDTO.getReviewCount() == null) {
                hospitalDTO.setReviewCount(0L);
            }

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

            if (pharmacyDTO.getRating() == null) {
                pharmacyDTO.setRating(0.0);
            }

            if (pharmacyDTO.getReviewCount() == null) {
                pharmacyDTO.setReviewCount(0L);
            }

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

            if (ambulanceProviderDTO.getRating() == null) {
                ambulanceProviderDTO.setRating(0.0);
            }

            if (ambulanceProviderDTO.getReviewCount() == null) {
                ambulanceProviderDTO.setReviewCount(0L);
            }

            userdetails = ambulanceProviderDTO;
        }

        if (userdetails == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Profile not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userdetails, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR','HOSPITAL','AMBULANCE','PHARMACY')")
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

        statisticsDTO.set_7DaysRating(reviewRepository.findAvgRatingByDate(user.getId(), sevenDaysAgo, now));
        statisticsDTO.set_30DaysRating(reviewRepository.findAvgRatingByDate(user.getId(), thirtyDaysAgo, now));
        statisticsDTO.setTotalRating(reviewRepository.findAvgRating(user.getId()));

        String roleType = user.getRoles().get(0).getRoleType();

        if ("DOCTOR".equalsIgnoreCase(roleType)) {
            // Doctor statistics logic
            statisticsDTO.set_7DaysCount(
                    doctorSerialRepository.countSerialsByDoctorAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(
                    doctorSerialRepository.countSerialsByDoctorAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(doctorSerialRepository.countSerialsByDoctor(user.getId()));
            statisticsDTO
                    .set_7DaysIncome(doctorSerialRepository.sumPriceByDoctorAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO
                    .set_30DaysIncome(doctorSerialRepository.sumPriceByDoctorAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(doctorSerialRepository.sumPriceByDoctor(user.getId()));

            List<Object[]> incomeList = doctorSerialRepository.sumPriceByDoctorAndDateGroupByDate(user.getId(),
                    thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else if ("PHARMACY".equalsIgnoreCase(roleType)) {
            // Pharmacy statistics logic
            statisticsDTO.set_7DaysCount(
                    medicineOrderRepository.countMedicineOrdersByPharmacyAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(
                    medicineOrderRepository.countMedicineOrdersByPharmacyAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(medicineOrderRepository.countMedicineOrdersByPharmacy(user.getId()));
            statisticsDTO.set_7DaysIncome(
                    medicineOrderRepository.sumPriceByPharmacyAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(
                    medicineOrderRepository.sumPriceByPharmacyAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(medicineOrderRepository.sumPriceByPharmacy(user.getId()));

            List<Object[]> incomeList = medicineOrderRepository.sumPriceByPharmacyAndDateGroupByDate(user.getId(),
                    thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else if ("HOSPITAL".equalsIgnoreCase(roleType)) {
            // Hospital statistics logic
            statisticsDTO.set_7DaysCount(
                    diagnosisOrderRepository.countDiagnosisOrdersByHospitalAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(
                    diagnosisOrderRepository.countDiagnosisOrdersByHospitalAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(diagnosisOrderRepository.countDiagnosisOrdersByHospital(user.getId()));
            statisticsDTO.set_7DaysIncome(
                    diagnosisOrderRepository.sumPriceByHospitalAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(
                    diagnosisOrderRepository.sumPriceByHospitalAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(diagnosisOrderRepository.sumPriceByHospital(user.getId()));

            List<Object[]> incomeList = diagnosisOrderRepository.sumPriceByHospitalAndDateGroupByDate(user.getId(),
                    thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else if ("AMBULANCE".equalsIgnoreCase(roleType)) {
            // Ambulance statistics logic
            statisticsDTO.set_7DaysCount(
                    ambulanceTripRepository.countTripsByAmbulanceProviderAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysCount(
                    ambulanceTripRepository.countTripsByAmbulanceProviderAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalCount(ambulanceTripRepository.countTripsByAmbulanceProvider(user.getId()));
            statisticsDTO.set_7DaysIncome(
                    ambulanceTripRepository.sumPriceByAmbulanceProviderAndDate(user.getId(), sevenDaysAgo, now));
            statisticsDTO.set_30DaysIncome(
                    ambulanceTripRepository.sumPriceByAmbulanceProviderAndDate(user.getId(), thirtyDaysAgo, now));
            statisticsDTO.setTotalIncome(ambulanceTripRepository.sumPriceByAmbulanceProvider(user.getId()));

            List<Object[]> incomeList = ambulanceTripRepository
                    .sumPriceByAmbulanceProviderAndDateGroupByDate(user.getId(), thirtyDaysAgo, now);

            statisticsDTO.setDates(new ArrayList<>());
            statisticsDTO.setIncomes(new ArrayList<>());

            for (var i : incomeList) {
                statisticsDTO.getDates().add((LocalDate) i[0]);
                statisticsDTO.getIncomes().add((Long) i[1]);
            }
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "invalid role type"), HttpStatus.BAD_REQUEST);
        }

        statisticsDTO.set_7DaysCount(statisticsDTO.get_7DaysCount() == null ? 0L : statisticsDTO.get_7DaysCount());
        statisticsDTO.set_30DaysCount(statisticsDTO.get_30DaysCount() == null ? 0L : statisticsDTO.get_30DaysCount());
        statisticsDTO.setTotalCount(statisticsDTO.getTotalCount() == null ? 0L : statisticsDTO.getTotalCount());
        statisticsDTO.set_7DaysIncome(statisticsDTO.get_7DaysIncome() == null ? 0L : statisticsDTO.get_7DaysIncome());
        statisticsDTO.set_30DaysIncome(statisticsDTO.get_30DaysIncome() == null ? 0L : statisticsDTO.get_30DaysIncome());
        statisticsDTO.setTotalIncome(statisticsDTO.getTotalIncome() == null ? 0L : statisticsDTO.getTotalIncome());
        statisticsDTO.set_7DaysRating(statisticsDTO.get_7DaysRating() == null ? 0L : statisticsDTO.get_7DaysRating());
        statisticsDTO.set_30DaysRating(statisticsDTO.get_30DaysRating() == null ? 0L : statisticsDTO.get_30DaysRating());
        statisticsDTO.setTotalRating(statisticsDTO.getTotalRating() == null ? 0L : statisticsDTO.getTotalRating());


        return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DOCTOR','HOSPITAL','AMBULANCE','PHARMACY','USER')")
    @DeleteMapping("/delete/user")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {

        AppUser appUser = userService.returnUser(request);

        if (appUser == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"), HttpStatus.NOT_FOUND);
        }

        Long id = appUser.getId();

        List<Role> roles = appUser.getRoles();

        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getRoleType().equals("USER")) {
                userRepository.delete(appUser);
            }
            if (roles.get(i).getRoleType().equals("DOCTOR")) {

                Optional<Doctor> doctor = doctorRepository.findByAppUser_Id(id);

                if (doctor.isEmpty()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"),
                            HttpStatus.BAD_REQUEST);
                }
                doctorRepository.delete(doctor.get());
            }
            if (roles.get(i).getRoleType().equals("HOSPITAL")) {
                Optional<Hospital> hospital = hospitalRepository.findByAppUser_Id(id);

                if (hospital.isEmpty()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"),
                            HttpStatus.BAD_REQUEST);
                }

                hospitalRepository.delete(hospital.get());
            }
            if (roles.get(i).getRoleType().equals("PHARMACY")) {
                Optional<Pharmacy> pharmacy = pharmacyRepository.findByAppUser_Id(id);

                if (pharmacy.isEmpty()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"),
                            HttpStatus.BAD_REQUEST);
                }

                pharmacyRepository.delete(pharmacy.get());
            }
            if (roles.get(i).getRoleType().equals("AMBULANCE")) {
                Optional<AmbulanceProvider> ambulanceProvider = ambulanceProviderRepository.findByAppUser_Id(id);

                if (ambulanceProvider.isEmpty()) {
                    return new ResponseEntity<>(ApiResponse.create("error", "User does not exist"),
                            HttpStatus.BAD_REQUEST);
                }

                ambulanceProviderRepository.delete(ambulanceProvider.get());
            }
        }

        return new ResponseEntity<>(ApiResponse.create("delete", "User is deleted"), HttpStatus.OK);
    }


    @GetMapping("bkash/execute/payment")
    public ResponseEntity<?> executePayment(@RequestParam(name = "paymentId") String paymentId,
                                            @RequestParam(name = "status") String status, @RequestParam(name = "type") String type,
                                            @RequestParam(name = "productId") Long productId) {
        if (status.equalsIgnoreCase("success")) {
            BkashExecuteResponse bkashExecuteResponse = bkashPaymentService.executePayment(paymentId);

            if (type.equalsIgnoreCase("DiagnosisOrder")) {
                Optional<DiagnosisOrder> optionalDiagnosisOrder = diagnosisOrderRepository.findById(productId);

                if (optionalDiagnosisOrder.isPresent()) {
                    DiagnosisOrder diagnosisOrder = optionalDiagnosisOrder.get();
                    diagnosisOrder.setTrxId(bkashExecuteResponse.getTrxID());
                    diagnosisOrderRepository.save(diagnosisOrder);

                    AppUser hospitalUser = diagnosisOrder.getHospital();
                    Optional<Hospital> hospital = hospitalRepository.findByAppUser_Id(hospitalUser.getId());

                    if (hospital.isPresent()) {
                        Hospital hospitalEntity = hospital.get();
                        hospitalEntity.setBalance(hospitalEntity.getBalance() + diagnosisOrder.getPrice());
                        hospitalRepository.save(hospitalEntity);
                    } else {
                        return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"),
                                HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(ApiResponse.create("error", "DiagnosisOrder not found"),
                            HttpStatus.NOT_FOUND);
                }
            } else if (type.equalsIgnoreCase("DoctorSerial")) {
                Optional<DoctorSerial> optionalDoctorSerial = doctorSerialRepository.findById(productId);

                if (optionalDoctorSerial.isPresent()) {
                    DoctorSerial doctorSerial = optionalDoctorSerial.get();
                    doctorSerial.setTrxId(bkashExecuteResponse.getTrxID());
                    doctorSerialRepository.save(doctorSerial);

                    AppUser doctorUser = doctorSerial.getDoctor();
                    Optional<Doctor> doctor = doctorRepository.findByAppUser_Id(doctorUser.getId());

                    if (doctor.isPresent()) {
                        Doctor doctorEntity = doctor.get();
                        doctorEntity.setBalance(doctorEntity.getBalance() + doctorSerial.getPrice());
                        doctorRepository.save(doctorEntity);
                    } else {
                        return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"),
                                HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(ApiResponse.create("error", "DoctorSerial not found"),
                            HttpStatus.NOT_FOUND);
                }
            } else if (type.equalsIgnoreCase("MedicineOrder")) {
                Optional<MedicineOrder> optionalMedicineOrder = medicineOrderRepository.findById(productId);

                if (optionalMedicineOrder.isPresent()) {
                    MedicineOrder medicineOrder = optionalMedicineOrder.get();
                    medicineOrder.setTrxId(bkashExecuteResponse.getTrxID());
                    medicineOrderRepository.save(medicineOrder);
                } else {
                    return new ResponseEntity<>(ApiResponse.create("error", "MedicineOrder not found"),
                            HttpStatus.NOT_FOUND);
                }
            } else if (type.equalsIgnoreCase("AmbulanceTrip")) {
                Optional<AmbulanceTrip> optionalAmbulanceTrip = ambulanceTripRepository.findById(productId);

                if (optionalAmbulanceTrip.isPresent()) {
                    AmbulanceTrip ambulanceTrip = optionalAmbulanceTrip.get();
                    ambulanceTrip.setTrxId(bkashExecuteResponse.getTrxID());
                    ambulanceTripRepository.save(ambulanceTrip);
                } else {
                    return new ResponseEntity<>(ApiResponse.create("error", "AmbulanceTrip not found"),
                            HttpStatus.NOT_FOUND);
                }
            }

            return new ResponseEntity<>(bkashExecuteResponse, HttpStatus.OK);
        } else {

            if (type.equalsIgnoreCase("DiagnosisOrder")) {
                diagnosisOrderRepository.deleteById(productId);
            } else if (type.equalsIgnoreCase("DoctorSerial")) {
                doctorSerialRepository.deleteById(productId);
            } else if (type.equalsIgnoreCase("MedicineOrder")) {
                medicineOrderRepository.deleteById(productId);
            } else if (type.equalsIgnoreCase("AmbulanceTrip")) {
                ambulanceTripRepository.deleteById(productId);
            }

            return new ResponseEntity<>(ApiResponse.create("error", "Product is canceled or not successful"),
                    HttpStatus.OK);
        }
    }


    @PostMapping("/add/doctorSerial")
    public ResponseEntity<?> createDoctorSerial(@RequestBody DoctorSerialDTO doctorSerialDTO,
                                                HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Optional<AppUser> opDoctor = userRepository.findById(doctorSerialDTO.getDoctor_id());

        if (opDoctor.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Dcotor not found"), HttpStatus.NOT_FOUND);
        }

        AppUser doctorUser = opDoctor.get();

        DoctorSerial doctorSerial = modelMapper.map(doctorSerialDTO, DoctorSerial.class);

        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(opDoctor.get().getId());

        if (optionalDoctor.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Dcotor not found"), HttpStatus.NOT_FOUND);
        }

        Doctor doctor = optionalDoctor.get();

        if (doctorSerialDTO.getType().equalsIgnoreCase("online")) {
            for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {
                if (doctor.getAvailableOnlineTimes().get(i).getDate().equals(doctorSerialDTO.getAppointmentDate())) {
                    doctor.getAvailableOnlineTimes().get(i).count++;
                    doctor.getAvailableOnlineTimes().get(i).setAvailTime(doctorService.setNewSerialTime(doctor.getAvailableOnlineTimes().get(i).getAvailTime()));
                }
            }
        }

        if (doctorSerialDTO.getType().equalsIgnoreCase("offline")) {
            for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {
                if (doctor.getAvailableTimes().get(i).getDate().equals(doctorSerialDTO.getAppointmentDate())) {
                    doctor.getAvailableTimes().get(i).count++;
                    doctor.getAvailableTimes().get(i).setAvailTime(doctorService.setNewSerialTime(doctor.getAvailableTimes().get(i).getAvailTime()));
                }
            }
        }

        doctor.balance += doctorSerial.getPrice() - AppConstants.perUserCharge;

        doctorRepository.save(doctor);

        doctorSerial.setDate(LocalDate.now());

        doctorSerial.setUser(user);
        doctorSerial.setDoctor(doctorUser);
        doctorSerial.setChecked(0);

        BkashCreateResponse bkashCreateResponse = bkashPaymentService.createPayment(doctorSerial.getPrice().toString());

        doctorSerial.setPrice(doctorSerial.getPrice()-AppConstants.perUserCharge);

        bkashCreateResponse.setType("DoctorSerial");

        DoctorSerial savedDoctorSerial = doctorSerialRepository.save(doctorSerial);

        bkashCreateResponse.setProductId(savedDoctorSerial.getId());

        savedDoctorSerial.setPaymentId(bkashCreateResponse.getPaymentId());

        doctorSerialRepository.save(savedDoctorSerial);

        return new ResponseEntity<>(bkashCreateResponse, HttpStatus.OK);
    }

    @PostMapping("/order/diagnosis")
    public ResponseEntity<?> createDiagnosisOrder(@RequestBody DiagnosisOrderDTO diagnosisOrderDTO,
                                                  HttpServletRequest request) {
        AppUser appUser = userService.returnUser(request);

        Optional<AppUser> opHospital = userRepository.findById(diagnosisOrderDTO.getHospitalId());

        if (opHospital.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital does not exist"), HttpStatus.BAD_REQUEST);
        }

        AppUser hospitalUser = opHospital.get();

        Optional<Hospital> optionalHospital = hospitalRepository.findByAppUser_Id(hospitalUser.getId());

        if (optionalHospital.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital Not Found"), HttpStatus.NOT_FOUND);
        }

        Hospital hospital = optionalHospital.get();

        DiagnosisOrder diagnosisOrder = new DiagnosisOrder();

        diagnosisOrder.setDescription(diagnosisOrderDTO.getDescription());
        diagnosisOrder.setPrice(diagnosisOrderDTO.getPrice());
        diagnosisOrder.setTime(diagnosisOrderDTO.getTime());
        diagnosisOrder.setUser(appUser);
        diagnosisOrder.setHospital(hospitalUser);
        diagnosisOrder.setOrderDate(diagnosisOrderDTO.getOrderDate());
        diagnosisOrder.setDate(LocalDate.now());
        diagnosisOrder.setChecked(0);

        hospital.balance += diagnosisOrder.getPrice() - AppConstants.perUserCharge;

        hospitalRepository.save(hospital);

        BkashCreateResponse bkashCreateResponse = bkashPaymentService
                .createPayment(diagnosisOrder.getPrice().toString());

        bkashCreateResponse.setType("DiagnosisOrder");

        diagnosisOrder.setPrice(diagnosisOrder.getPrice()-AppConstants.perUserCharge);

        DiagnosisOrder savedDiagnosisOrder = diagnosisOrderRepository.save(diagnosisOrder);

        bkashCreateResponse.setProductId(savedDiagnosisOrder.getId());

        savedDiagnosisOrder.setPaymentId(bkashCreateResponse.getPaymentId());

        diagnosisOrderRepository.save(savedDiagnosisOrder);

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

        BkashCreateResponse bkashCreateResponse = bkashPaymentService
                .createPayment(medicineOrder.getPrice().toString());

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

        ambulanceTrip.setDate(LocalDate.now());
        ambulanceTrip.setChecked(0);

        ambulanceTripRepository.save(ambulanceTrip);

        return new ResponseEntity<>(ApiResponse.create("create", "Trip created"), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('USER','AMBULANCE')")
    @DeleteMapping("/delete/ambulance/trip/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable(name = "id") Long id) {
        try {
            ambulanceTripRepository.deleteById(id);
            return new ResponseEntity<>(ApiResponse.create("delete", "Trip is deleted"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.create("error", "Trip can't be deleted"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add/review")
    public ResponseEntity<?> saveReview(@RequestParam(name = "review") String reviewStr,
                                        @RequestParam(name = "star") Long starCount, @RequestBody ReviewPendingDTO reviewPendingDTO,
                                        HttpServletRequest request) {

        AppUser reviewer = userService.returnUser(request);
        Optional<AppUser> optionalSubject = userRepository.findById(reviewPendingDTO.getSubjectId());

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
        review.setDate(LocalDate.now());

        reviewRepository.save(review);

        if (reviewPendingDTO.getRole().equalsIgnoreCase("Doctor")) {
            reviewService.updateReviewCheckedForDoctorSerial(reviewPendingDTO.getOrderId(), reviewer.getId(),
                    subject.getId());
        } else if (reviewPendingDTO.getRole().equalsIgnoreCase("Hospital")) {
            reviewService.updateReviewCheckedForDiagnosisOrder(reviewPendingDTO.getOrderId(), reviewer.getId(),
                    subject.getId());
        } else if (reviewPendingDTO.getRole().equalsIgnoreCase("Ambulance")) {
            reviewService.updateReviewCheckedForAmbulanceTrip(reviewPendingDTO.getOrderId(), reviewer.getId(),
                    subject.getId());
        } else if (reviewPendingDTO.getRole().equalsIgnoreCase("Pharmacy")) {
            reviewService.updateReviewCheckedForMedicineOrder(reviewPendingDTO.getOrderId(), reviewer.getId(), subject.getId());
        }

        ApiResponse createResponse = ApiResponse.create("create", "Review Saved");
        return new ResponseEntity<>(createResponse, HttpStatus.OK);

    }

    @GetMapping("/review/pending")
    public ResponseEntity<?> getPendingReview(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository.findDiagnosisOrderByReviewChecked(user.getId());

        List<DoctorSerial> doctorSerials = doctorSerialRepository.findDoctorSerialByReviewChecked(user.getId());

        List<AmbulanceTrip> ambulanceTrips = ambulanceTripRepository.findTripByReviewChecked(user.getId());

        List<MedicineOrder> medicineOrders = medicineOrderRepository.findMedicineOrderByReviewChecked(user.getId());

        List<ReviewPendingDTO> reviewPendingDTOS = new ArrayList<>();

        for (var diagnosisOrder : diagnosisOrders) {
            ReviewPendingDTO reviewPendingDTO = new ReviewPendingDTO();
            reviewPendingDTO.setOrderId(diagnosisOrder.getId());
            reviewPendingDTO.setRole("Hospital");
            reviewPendingDTO.setSubjectName(
                    hospitalRepository.findByAppUser_Id(diagnosisOrder.getHospital().getId()).get().getHospitalName());
            reviewPendingDTO.setSubjectId(diagnosisOrder.getHospital().getId());
            reviewPendingDTOS.add(reviewPendingDTO);
        }

        for (var doctorSerial : doctorSerials) {
            ReviewPendingDTO reviewPendingDTO = new ReviewPendingDTO();
            reviewPendingDTO.setOrderId(doctorSerial.getId());
            reviewPendingDTO.setRole("Doctor");
            reviewPendingDTO.setSubjectName(
                    doctorSerial.getDoctor().getFirstName() + " " + doctorSerial.getDoctor().getLastName());
            reviewPendingDTO.setSubjectId(doctorSerial.getDoctor().getId());
            reviewPendingDTOS.add(reviewPendingDTO);
        }

        for (var ambulanceTrip : ambulanceTrips) {
            ReviewPendingDTO reviewPendingDTO = new ReviewPendingDTO();
            reviewPendingDTO.setOrderId(ambulanceTrip.getId());
            reviewPendingDTO.setRole("Ambulance");
            reviewPendingDTO.setSubjectName(ambulanceTrip.getAmbulanceProvider().getFirstName() + " "
                    + ambulanceTrip.getAmbulanceProvider().getLastName());
            reviewPendingDTO.setSubjectId(ambulanceTrip.getAmbulanceProvider().getId());
            reviewPendingDTOS.add(reviewPendingDTO);
        }

        for (var medicineOrder : medicineOrders) {
            ReviewPendingDTO reviewPendingDTO = new ReviewPendingDTO();
            reviewPendingDTO.setOrderId(medicineOrder.getId());
            reviewPendingDTO.setRole("Pharmacy");
            reviewPendingDTO.setSubjectName(medicineOrder.getPharmacy().getFirstName() + " "
                    + medicineOrder.getPharmacy().getLastName());
            reviewPendingDTO.setSubjectId(medicineOrder.getPharmacy().getId());
            reviewPendingDTOS.add(reviewPendingDTO);
        }

        if (reviewPendingDTOS.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending response found"), HttpStatus.OK);
        }

        return new ResponseEntity<>(reviewPendingDTOS, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/add/response")
    public ResponseEntity<?> addUserResponse(@RequestParam(name = "name") String name, @RequestParam(name = "email") String email, @RequestParam(name = "message") String message) {

        UserResponse userResponse = new UserResponse();
        userResponse.setDate(LocalDate.now());
        userResponse.setName(name);
        userResponse.setEmail(email);
        userResponse.setMessage(message);
        userResponse.setChecked(0);
        userResponseRepository.save(userResponse);

        return new ResponseEntity<>(ApiResponse.create("create", "Response created"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/add/notification")
    public ResponseEntity<?>addNotification(HttpServletRequest request, @RequestBody AppUserNotification userNotification)
    {
        userNotification.setChecked(false);

        appUserNotificationRepository.save(userNotification);

        return new ResponseEntity<>(ApiResponse.create("create", "Notification is created"),HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/user/notification/unchecked")
    public ResponseEntity<?>getUncheckedNotifications(HttpServletRequest request)
    {
        AppUser user = userService.returnUser(request);

        List<AppUserNotification>userNotifications = appUserNotificationRepository.findByCheckedFalseAndUserId(user.getId());

        if(userNotifications.isEmpty())
        {
            return new ResponseEntity<>(ApiResponse.create("empty","No unchecked found"),HttpStatus.OK);
        }

        List<AppUserNotificationDTO>notificationDTOS = new ArrayList<>();

        for(var notification:userNotifications)
        {
            AppUserNotificationDTO userNotificationDTO = modelMapper.map(notification, AppUserNotificationDTO.class);
            notification.setChecked(true);
            appUserNotificationRepository.save(notification);
            notificationDTOS.add(userNotificationDTO);
        }

        return new ResponseEntity<>(notificationDTOS, HttpStatus.OK);
    }

    @GetMapping("/dashboard/reports&prescriptions")
    public ResponseEntity<?> getAllReportsPrescriptions(HttpServletRequest request) {

        AppUser appUser = userService.returnUser(request);

        List<DoctorSerial> doctorSerials = doctorSerialRepository
                .findByUser_IdAndPrescriptionIsNotNull(appUser.getId());

        List<DiagnosisOrder> diagnosisOrders = diagnosisOrderRepository
                .findByUser_IdAndReportURLIsNotNull(appUser.getId());

        rpDTO rps = new rpDTO();

        rps.setAllPrescriptions(new ArrayList<>());
        rps.setAllReports(new ArrayList<>());

        for (int i = 0; i < doctorSerials.size(); i++) {
            DoctorPrescriptionDTO doctorPrescriptionDTO = new DoctorPrescriptionDTO();

            doctorPrescriptionDTO.setPrescription(doctorSerials.get(i).getPrescription());
            doctorPrescriptionDTO.setDoctorName(doctorSerials.get(i).getDoctor().getFirstName() + " "
                    + doctorSerials.get(i).getDoctor().getLastName());
            doctorPrescriptionDTO.setDoctorId(doctorSerials.get(i).getDoctor().getId());

            rps.getAllPrescriptions().add(doctorPrescriptionDTO);
        }

        for (int i = 0; i < diagnosisOrders.size(); i++) {
            DiagnosisReportDTO diagnosisReportDTO = new DiagnosisReportDTO();

            diagnosisReportDTO.setDescription(diagnosisOrders.get(i).getDescription());
            diagnosisReportDTO.setReportURL(diagnosisOrders.get(i).getReportURL());
            diagnosisReportDTO.setHospitalId(diagnosisOrders.get(i).getHospital().getId());
            diagnosisReportDTO.setHospitalName(hospitalRepository
                    .findByAppUser_Id(diagnosisOrders.get(i).getHospital().getId()).get().getHospitalName());

            rps.getAllReports().add(diagnosisReportDTO);
        }

        return new ResponseEntity<>(rps, HttpStatus.OK);
    }

    @GetMapping("/dashboard/user/upcoming/doctorserial")
    public ResponseEntity<?> getAllUpcomingForUser(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now(ZoneId.of("Asia/Dhaka")).minusMinutes(30));


        List<DoctorSerial> upcomingDoctorSerials = doctorSerialRepository.findByDateAndTimeAndUserId(LocalDate.now(ZoneId.of("Asia/Dhaka")), time,
                user.getId());

        if (upcomingDoctorSerials.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<UserDoctorSerialViewDTO> userDoctorSerialDTOs = new ArrayList<>();

        for (var doctorSerial : upcomingDoctorSerials) {
            UserDoctorSerialViewDTO userDoctorSerialDTO = new UserDoctorSerialViewDTO();
            userDoctorSerialDTO.setId(doctorSerial.getId());
            userDoctorSerialDTO.setTime(doctorSerial.getTime());
            userDoctorSerialDTO.setDate(doctorSerial.getAppointmentDate());
            userDoctorSerialDTO.setDoctorName(
                    doctorSerial.getDoctor().getFirstName() + " " + doctorSerial.getDoctor().getLastName());
            userDoctorSerialDTO.setDoctorId(doctorSerial.getDoctor().getId());
            userDoctorSerialDTO.setContactNo(doctorSerial.getDoctor().getContactNo());
            userDoctorSerialDTO.setType(doctorSerial.getType());

            userDoctorSerialDTOs.add(userDoctorSerialDTO);
        }

        return new ResponseEntity<>(userDoctorSerialDTOs, HttpStatus.OK);
    }

    @GetMapping("/dashboard/user/upcoming/diagnosis")
    public ResponseEntity<?> getAllUpcoming(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        Double time = timeService.convertTimeToDouble(LocalTime.now(ZoneId.of("Asia/Dhaka")).minusMinutes(30));

        List<DiagnosisOrder> upcomingDiagnosisOrders = diagnosisOrderRepository
                .findByDateAndTimeAndUserId(LocalDate.now(ZoneId.of("Asia/Dhaka")), time, user.getId());

        if (upcomingDiagnosisOrders.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming found"), HttpStatus.OK);
        }

        List<UserDiagnosisOrderViewDTO> userDiagnosisOrderViewDTOS = new ArrayList<>();

        for (var diagnosisOrder : upcomingDiagnosisOrders) {
            UserDiagnosisOrderViewDTO userDiagnosisOrderViewDTO = new UserDiagnosisOrderViewDTO();
            userDiagnosisOrderViewDTO.setId(diagnosisOrder.getId());
            userDiagnosisOrderViewDTO.setDescription(diagnosisOrder.getDescription());
            userDiagnosisOrderViewDTO.setTime(diagnosisOrder.getTime());
            userDiagnosisOrderViewDTO.setPlace(diagnosisOrder.getPlace());
            userDiagnosisOrderViewDTO.setHospitalId(diagnosisOrder.getHospital().getId());
            userDiagnosisOrderViewDTO.setContactNo(diagnosisOrder.getHospital().getContactNo());
            userDiagnosisOrderViewDTO.setDate(diagnosisOrder.getOrderDate());
            userDiagnosisOrderViewDTO.setDeptContactNo(diagnosisOrder.getDeptContactNo());
            userDiagnosisOrderViewDTO.setHospitalName(
                    hospitalRepository.findByAppUser_Id(diagnosisOrder.getHospital().getId()).get().getHospitalName());

            userDiagnosisOrderViewDTOS.add(userDiagnosisOrderViewDTO);
        }

        return new ResponseEntity<>(userDiagnosisOrderViewDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('AMBULANCE','USER')")
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

        if (ambulanceTrips.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No upcoming trip found"), HttpStatus.OK);
        }

        List<AmbulanceTripViewDTO> ambulanceTripViewDTOS = new ArrayList<>();

        for (var ambulanceTrip : ambulanceTrips) {
            AmbulanceTripViewDTO ambulanceTripViewDTO = new AmbulanceTripViewDTO();
            ambulanceTripViewDTO.setId(ambulanceTrip.getId());
            ambulanceTripViewDTO.setUserId(ambulanceTrip.getUser().getId());
            ambulanceTripViewDTO.setProviderId(ambulanceTrip.getAmbulanceProvider().getId());
            ambulanceTripViewDTO
                    .setUserName(ambulanceTrip.getUser().getFirstName() + " " + ambulanceTrip.getUser().getLastName());
            ambulanceTripViewDTO.setProviderName(ambulanceTrip.getAmbulanceProvider().getFirstName() + " "
                    + ambulanceTrip.getAmbulanceProvider().getLastName());
            ambulanceTripViewDTO.setUserContactNo(ambulanceTrip.getUser().getContactNo());
            ambulanceTripViewDTO.setSource(ambulanceTrip.getSource());
            ambulanceTripViewDTO.setDestination(ambulanceTrip.getDestination());
            ambulanceTripViewDTO.setLocation(ambulanceTrip.getLocation());
            ambulanceTripViewDTO.setPrice(ambulanceTrip.getPrice());
            ambulanceTripViewDTO.setOrderDate(ambulanceTrip.getOrderDate());

            ambulanceTripViewDTOS.add(ambulanceTripViewDTO);
        }

        return new ResponseEntity<>(ambulanceTripViewDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('USER','AMBULANCE')")
    @GetMapping("/ambulance/trip/pending")
    public ResponseEntity<?> getAllPendingTrips(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);

        List<AmbulanceTrip> ambulanceTrips = ambulanceTripRepository.findTripsByUserProviderNULL(user.getId());

        if (ambulanceTrips.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending  Trip  found"), HttpStatus.OK);
        }

        List<AmbulanceTripPendingViewDTO> ambulanceTripPendingViewDTOS = new ArrayList<>();

        for (var i : ambulanceTrips) {
            AmbulanceTripPendingViewDTO ambulanceTripPendingViewDTO = new AmbulanceTripPendingViewDTO();
            ambulanceTripPendingViewDTO.setId(i.getId());
            ambulanceTripPendingViewDTO.setPrice(i.getPrice());
            ambulanceTripPendingViewDTO.setSource(i.getSource());
            ambulanceTripPendingViewDTO.setDestination(i.getDestination());
            ambulanceTripPendingViewDTO.setLocation(i.getLocation());
            ambulanceTripPendingViewDTO.setOrderDate(i.getOrderDate());

            List<BidderDTO> bidderDTOS = new ArrayList<>();

            for (var j : i.getBidders()) {
                BidderDTO bidderDTO = new BidderDTO();
                bidderDTO.setId(j.getId());
                bidderDTO.setName(j.getFirstName() + " " + j.getLastName());
                bidderDTO.setEmail(j.getEmail());
                bidderDTO.setContactNo(j.getContactNo());
                bidderDTO.setRating(reviewRepository.findAvgRating(j.getId()));

                if (bidderDTO.getRating() == null) {
                    bidderDTO.setRating(0.0);
                }
                bidderDTOS.add(bidderDTO);
            }

            ambulanceTripPendingViewDTO.setBidders(bidderDTOS);

            ambulanceTripPendingViewDTOS.add(ambulanceTripPendingViewDTO);
        }

        return new ResponseEntity<>(ambulanceTripPendingViewDTOS, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyAuthority('PHARMACY','USER')")
    @GetMapping("/medicine/order/undelivered")
    public ResponseEntity<?> getAllUndelivered(HttpServletRequest request) {
        AppUser user = userService.returnUser(request);
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "user not found"), HttpStatus.NOT_FOUND);
        }

        String roleType = user.getRoles().get(0).getRoleType();

        List<MedicineOrder> medicineOrders = new ArrayList<>();

        if (roleType.equalsIgnoreCase("USER")) {
            medicineOrders = medicineOrderRepository.findUndeliveredOrdersByUser(user.getId());
        } else if (roleType.equalsIgnoreCase("PHARMACY")) {
            medicineOrders = medicineOrderRepository.findUndeliveredOrdersByPharmacy(user.getId());
        }

        if (medicineOrders.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "Undelivered not found"), HttpStatus.OK);
        }

        List<MedicineOrderViewDTO> medicineOrderViewDTOS = new ArrayList<>();

        for (var medicineOrder : medicineOrders) {
            MedicineOrderViewDTO medicineOrderViewDTO = new MedicineOrderViewDTO();
            medicineOrderViewDTO.setId(medicineOrder.getId());
            medicineOrderViewDTO.setUserId(medicineOrder.getUser().getId());
            medicineOrderViewDTO.setPlace(medicineOrder.getPlace());
            medicineOrderViewDTO.setContactNo(medicineOrder.getUser().getContactNo());
            medicineOrderViewDTO.setPharmacyId(medicineOrder.getPharmacy().getId());
            medicineOrderViewDTO
                    .setUserName(medicineOrder.getUser().getFirstName() + " " + medicineOrder.getUser().getLastName());
            medicineOrderViewDTO.setPharmacyName(
                    medicineOrder.getPharmacy().getFirstName() + " " + medicineOrder.getPharmacy().getLastName());
            medicineOrderViewDTO.setDescription(medicineOrder.getDescription());
            medicineOrderViewDTO.setPrice(medicineOrder.getPrice());
            medicineOrderViewDTO.setUserEmail(medicineOrder.getUser().getEmail());
            medicineOrderViewDTO.setPharmacyEmail(medicineOrder.getPharmacy().getEmail());

            medicineOrderViewDTOS.add(medicineOrderViewDTO);
        }

        return new ResponseEntity<>(medicineOrderViewDTOS, HttpStatus.OK);
    }
}


