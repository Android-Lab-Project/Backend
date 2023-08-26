package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.StatisticsDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.MedicineOrderRepository;
import com.healthtechbd.backend.service.UserService;
import com.healthtechbd.backend.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class PharmacyController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/pharmacy/statistics")
    public ResponseEntity<?> getPharmacyStatistics(HttpServletRequest request) {
        AppUser pharmacy = userService.returnUser(request);
        if (pharmacy == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "pharmacy not found"), HttpStatus.OK);
        }

        StatisticsDTO statisticsDTO = new StatisticsDTO();

        statisticsDTO.set_7DaysCount(medicineOrderRepository.countSerialsByPharmacyAndDate(pharmacy.getId(), LocalDate.now().minusDays(7), LocalDate.now()));

        statisticsDTO.set_30DaysCount(medicineOrderRepository.countSerialsByPharmacyAndDate(pharmacy.getId(), LocalDate.now().minusDays(30), LocalDate.now()));

        statisticsDTO.setTotalCount(medicineOrderRepository.countSerialsByPharmacy(pharmacy.getId()));

        statisticsDTO.set_7DaysIncome(medicineOrderRepository.sumPriceByPharmacyAndDate(pharmacy.getId(), LocalDate.now().minusDays(7), LocalDate.now()));
        statisticsDTO.set_30DaysIncome(medicineOrderRepository.sumPriceByPharmacyAndDate(pharmacy.getId(), LocalDate.now().minusDays(30), LocalDate.now()));
        statisticsDTO.setTotalIncome(medicineOrderRepository.sumPriceByPharmacy(pharmacy.getId()));

        List<Object[]> incomeList = medicineOrderRepository.sumPriceByPharmacyAndDateGroupByDate(pharmacy.getId(), LocalDate.now().minusDays(30), LocalDate.now());

        statisticsDTO.setDates(new ArrayList<>());

        statisticsDTO.setIncomes(new ArrayList<>());

        for (var i : incomeList) {
            statisticsDTO.getDates().add((LocalDate) i[0]);
            statisticsDTO.getIncomes().add((Long) i[1]);
        }

        return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
    }

}
