package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.StatisticsDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DiagnosisOrderRepository;
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
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class HospitalController {


    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @GetMapping("/hospital/statistics")
    public ResponseEntity<?> getHospitalStatistics(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("username");

        Optional<AppUser> optionalAppUser = userRepository.findByEmail(userEmail);

        AppUser hospital = new AppUser();

        if (optionalAppUser.isPresent()) {
            hospital = optionalAppUser.get();
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.OK);
        }

        StatisticsDTO statisticsDTO = new StatisticsDTO();

        statisticsDTO.set_7DaysCount(diagnosisOrderRepository.countSerialsByHospitalAndDate(hospital.getId(), LocalDate.now().minusDays(7), LocalDate.now()));

        statisticsDTO.set_30DaysCount(diagnosisOrderRepository.countSerialsByHospitalAndDate(hospital.getId(), LocalDate.now().minusDays(30), LocalDate.now()));

        statisticsDTO.setTotalCount(diagnosisOrderRepository.countSerialsByHospital(hospital.getId()));

        statisticsDTO.set_7DaysIncome(diagnosisOrderRepository.sumPriceByHospitalAndDate(
                hospital.getId(), LocalDate.now().minusDays(7), LocalDate.now()));


        statisticsDTO.set_30DaysIncome(diagnosisOrderRepository.sumPriceByHospitalAndDate(
                hospital.getId(), LocalDate.now().minusDays(30), LocalDate.now()));

        statisticsDTO.setTotalIncome(diagnosisOrderRepository.sumPriceByHospital(hospital.getId()));

        List<Object[]> incomeList = diagnosisOrderRepository.sumPriceByHospitalAndDateGroupByDate(
                hospital.getId(), LocalDate.now().minusDays(30), LocalDate.now());

        statisticsDTO.setDates(new ArrayList<>());
        statisticsDTO.setIncomes(new ArrayList<>());

        for (var i : incomeList) {
            statisticsDTO.getDates().add((LocalDate) i[0]);
            statisticsDTO.getIncomes().add((Long) i[1]);
        }

        return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
    }

}
