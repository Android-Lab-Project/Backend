package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AdminStatisticsDTO;
import com.healthtechbd.backend.entity.Admin;
import com.healthtechbd.backend.entity.UserResponse;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserCountStatsRepository userCountStatsRepository;

    @Autowired
    private UserResponseRepository userResponseRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Autowired
    private AmbulanceTripRepository ambulanceTripRepository;

    @GetMapping("/statistics/admin")
    public ResponseEntity<AdminStatisticsDTO> getAdminStats() {
        AdminStatisticsDTO adminStatisticsDTO = new AdminStatisticsDTO();

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        LocalDate thirtyDaysAgo = today.minusDays(30);

        adminStatisticsDTO.set_7daysUserCount(userCountStatsRepository.countUserByDate(sevenDaysAgo, today));
        adminStatisticsDTO.set_30daysUserCount(userCountStatsRepository.countUserByDate(thirtyDaysAgo, today));
        adminStatisticsDTO.setTotalUserCount(userCountStatsRepository.countUser());

        adminStatisticsDTO
                .setTotalDoctorSerialCount(doctorSerialRepository.countDoctorSerialsByDate(sevenDaysAgo, today));
        adminStatisticsDTO
                .setTotalDiagnosisOrderCount(diagnosisOrderRepository.countDiagnosisOrdersByDate(sevenDaysAgo, today));
        adminStatisticsDTO
                .setTotalMedicineOrderCount(medicineOrderRepository.countMedicineOrdersByDate(sevenDaysAgo, today));
        adminStatisticsDTO
                .setTotalAmbulanceTripCount(ambulanceTripRepository.countAmbulanceTripsByDate(sevenDaysAgo, today));

        Long _7daysDSIncome = doctorSerialRepository.countDoctorSerialsByDate(sevenDaysAgo, today)
                * AppConstants.perUserCharge;
        Long _30daysDSIncome = doctorSerialRepository.countDoctorSerialsByDate(thirtyDaysAgo, today)
                * AppConstants.perUserCharge;
        Long totalDSIncome = doctorSerialRepository.countDoctorSerials() * AppConstants.perUserCharge;

        Long _7daysDOIncome = diagnosisOrderRepository.countDiagnosisOrdersByDate(sevenDaysAgo, today)
                * AppConstants.perUserCharge;
        Long _30daysDOIncome = diagnosisOrderRepository.countDiagnosisOrdersByDate(thirtyDaysAgo, today)
                * AppConstants.perUserCharge;
        Long totalDOIncome = diagnosisOrderRepository.countDiagnosisOrders() * AppConstants.perUserCharge;

        Long _7daysMOIncome = medicineOrderRepository.countMedicineOrdersByDate(sevenDaysAgo, today)
                * AppConstants.perUserCharge;
        Long _30daysMOIncome = medicineOrderRepository.countMedicineOrdersByDate(thirtyDaysAgo, today)
                * AppConstants.perUserCharge;
        Long totalMOIncome = medicineOrderRepository.countMedicineOrders() * AppConstants.perUserCharge;

        Long _7daysATIncome = ambulanceTripRepository.countAmbulanceTripsByDate(sevenDaysAgo, today)
                * AppConstants.perUserCharge;
        Long _30daysATIncome = ambulanceTripRepository.countAmbulanceTripsByDate(thirtyDaysAgo, today)
                * AppConstants.perUserCharge;
        Long totalATIncome = ambulanceTripRepository.countAmbulanceTrips() * AppConstants.perUserCharge;

        adminStatisticsDTO.set_7daysIncome(_7daysDSIncome + _7daysDOIncome + _7daysMOIncome + _7daysATIncome);
        adminStatisticsDTO.set_30daysIncome(_30daysDSIncome + _30daysDOIncome + _30daysMOIncome + _30daysATIncome);
        adminStatisticsDTO.setTotalIncome(totalDSIncome + totalDOIncome + totalMOIncome + totalATIncome);

        Optional<Admin> optionalAdmin = adminRepository.findById(1L);

        optionalAdmin
                .ifPresent(admin -> admin.setBalance(totalDSIncome + totalDOIncome + totalMOIncome + totalATIncome));

        adminRepository.save(optionalAdmin.orElse(null));

        List<Object[]> doctorserials = doctorSerialRepository.countDoctorSerialsGroupByDate(thirtyDaysAgo, today);
        List<Object[]> diagnosisOrders = diagnosisOrderRepository.countDiagnosisOrdersGroupByDate(thirtyDaysAgo, today);
        List<Object[]> medicineOrders = medicineOrderRepository.countMedicineOrdersGroupByDate(thirtyDaysAgo, today);
        List<Object[]> ambulanceTrips = ambulanceTripRepository.countAmbulanceTripsGroupByDate(thirtyDaysAgo, today);

        List<LocalDate> dates = new ArrayList<>();
        List<Long> incomes = new ArrayList<>();

        int ds = 0, dos = 0, mo = 0, at = 0;

        for (int i = 0; i < 30; i++) {
            Long count = 0L;
            LocalDate date = today.minusDays(i);
            Object[] objects = new Object[2];

            if (ds < doctorserials.size()) {
                objects = doctorserials.get(ds);
            }

            if (objects != null && date.equals(objects[0])) {
                count += (Long) objects[1];
                ds++;
            }

            objects = null;

            if (dos < diagnosisOrders.size()) {
                objects = diagnosisOrders.get(dos);
            }

            if (objects != null && date.equals(objects[0])) {
                count += (Long) objects[1];
                dos++;
            }

            objects = null;

            if (mo < medicineOrders.size()) {
                objects = medicineOrders.get(mo);
            }

            if (objects != null && date.equals(objects[0])) {
                count += (Long) objects[1];
                mo++;
            }

            objects = null;

            if (at < ambulanceTrips.size()) {
                objects = ambulanceTrips.get(at);
            }

            if (objects != null && date.equals(objects[0])) {
                count += (Long) objects[1];
                at++;
            }

            if (count != 0) {
                System.out.println(count);
                dates.add(date);
                incomes.add(count * AppConstants.perUserCharge);
            }
        }

        Collections.reverse(dates);
        Collections.reverse(incomes);

        adminStatisticsDTO.setDates(dates);
        adminStatisticsDTO.setIncomes(incomes);

        return new ResponseEntity<>(adminStatisticsDTO, HttpStatus.OK);
    }

    @GetMapping("/update/user/response/{id}")
    public ResponseEntity<ApiResponse> updateUserResponse(@PathVariable(name = "id") Long id) {
        Optional<UserResponse> optionalUserResponse = userResponseRepository.findById(id);

        if (optionalUserResponse.isPresent()) {
            UserResponse userResponse = optionalUserResponse.get();
            userResponse.setChecked(1);
            userResponseRepository.save(userResponse);
            return new ResponseEntity<>(ApiResponse.create("update", "Response is checked"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ApiResponse.create("not_found", "User response not found"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("user/response/pending")
    public ResponseEntity<?> getAllPendingResponses() {
        List<UserResponse> userResponses = userResponseRepository.findByChecked();

        if (userResponses.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.create("empty", "No pending responses found"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userResponses, HttpStatus.OK);
        }
    }
}
