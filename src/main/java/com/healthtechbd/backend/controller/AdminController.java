package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.AdminStatisticsDTO;
import com.healthtechbd.backend.entity.Admin;
import com.healthtechbd.backend.entity.UserResponse;
import com.healthtechbd.backend.repo.*;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.AppConstants;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
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
  public ResponseEntity<?>getAdminStats()
  {
      AdminStatisticsDTO adminStatisticsDTO = new AdminStatisticsDTO();

      adminStatisticsDTO.set_7daysUserCount(userCountStatsRepository.countUserByDate(LocalDate.now().minusDays(7),LocalDate.now()));
      adminStatisticsDTO.set_30daysUserCount(userCountStatsRepository.countUserByDate(LocalDate.now().minusDays(30),LocalDate.now()));
      adminStatisticsDTO.setTotalUserCount(userCountStatsRepository.countUser());

      adminStatisticsDTO.setTotalDoctorSerialCount(doctorSerialRepository.countDoctorSerialsByDate(LocalDate.now().minusDays(7),LocalDate.now()));
      adminStatisticsDTO.setTotalDiagnosisOrderCount(diagnosisOrderRepository.countDiagnosisOrdersByDate(LocalDate.now().minusDays(7),LocalDate.now()));
      adminStatisticsDTO.setTotalMedicineOrderCount(medicineOrderRepository.countMedicineOrdersByDate(LocalDate.now().minusDays(7),LocalDate.now()));
      adminStatisticsDTO.setTotalAmbulanceTripCount(ambulanceTripRepository.countAmbulanceTripsByDate(LocalDate.now().minusDays(7),LocalDate.now()));

      Long _7daysDSIncome = doctorSerialRepository.countDoctorSerialsByDate(LocalDate.now().minusDays(7),LocalDate.now())* AppConstants.perUserCharge;
      Long _30daysDSIncome = doctorSerialRepository.countDoctorSerialsByDate(LocalDate.now().minusDays(30),LocalDate.now())* AppConstants.perUserCharge;
      Long totalDSIncome = doctorSerialRepository.countDoctorSerials()*AppConstants.perUserCharge;


      Long _7daysDOIncome = diagnosisOrderRepository.countDiagnosisOrdersByDate(LocalDate.now().minusDays(7), LocalDate.now()) * AppConstants.perUserCharge;
      Long _30daysDOIncome = diagnosisOrderRepository.countDiagnosisOrdersByDate(LocalDate.now().minusDays(30), LocalDate.now()) * AppConstants.perUserCharge;
      Long totalDOIncome = diagnosisOrderRepository.countDiagnosisOrders() * AppConstants.perUserCharge;


      Long _7daysMOIncome = medicineOrderRepository.countMedicineOrdersByDate(LocalDate.now().minusDays(7), LocalDate.now()) * AppConstants.perUserCharge;
      Long _30daysMOIncome = medicineOrderRepository.countMedicineOrdersByDate(LocalDate.now().minusDays(30), LocalDate.now()) * AppConstants.perUserCharge;
      Long totalMOIncome = medicineOrderRepository.countMedicineOrders() * AppConstants.perUserCharge;


      Long _7daysATIncome = ambulanceTripRepository.countAmbulanceTripsByDate(LocalDate.now().minusDays(7), LocalDate.now()) * AppConstants.perUserCharge;
      Long _30daysATIncome = ambulanceTripRepository.countAmbulanceTripsByDate(LocalDate.now().minusDays(30), LocalDate.now()) * AppConstants.perUserCharge;
      Long totalATIncome = ambulanceTripRepository.countAmbulanceTrips() * AppConstants.perUserCharge;

      adminStatisticsDTO.set_7daysIncome(_7daysDSIncome+_7daysDOIncome+_7daysMOIncome+_7daysATIncome);
      adminStatisticsDTO.set_30daysIncome(_30daysDSIncome+_30daysDOIncome+_30daysMOIncome+_30daysATIncome);
      adminStatisticsDTO.setTotalIncome(totalDSIncome+totalDOIncome+totalMOIncome+totalATIncome);

      Optional<Admin> optionalAdmin = adminRepository.findById(1L);

      Admin admin = optionalAdmin.get();

      admin.setBalance(totalDSIncome+totalDOIncome+totalMOIncome+totalATIncome);

      adminRepository.save(admin);

      List<Object[]>doctorserials = doctorSerialRepository.countDoctorSerialsGroupByDate(LocalDate.now().minusDays(30),LocalDate.now());

      List<Object[]>diagnosisOrders = diagnosisOrderRepository.countDiagnosisOrdersGroupByDate(LocalDate.now().minusDays(30),LocalDate.now());

      List<Object[]>medicineOrders = medicineOrderRepository.countMedicineOrdersGroupByDate(LocalDate.now().minusDays(30),LocalDate.now());

      List<Object[]>ambulanceTrips = ambulanceTripRepository.countAmbulanceTripsGroupByDate(LocalDate.now().minusDays(30),LocalDate.now());

      List<LocalDate>dates = new ArrayList<>();

      List<Long>incomes = new ArrayList<>();

      Integer ds=0;
      Integer dos=0;
      Integer mo=0;
      Integer at=0;

      for(int i=0;i<30;i++)
      {
          Long count =0L;

          LocalDate date = LocalDate.now().minusDays(i);

          dates.add(date);

          Object[] objects = new Object[2];

          if(ds<doctorserials.size()) {
            objects = doctorserials.get(ds);
          }

          if(date.equals(objects[0]))
          {
              count+=(Long)objects[1];
              ds++;
          }

          if(dos<diagnosisOrders.size())
          {
              objects = diagnosisOrders.get(dos);
          }

          if(date.equals(objects[0]))
          {
              count+=(Long)objects[1];
              dos++;
          }

          if(mo<medicineOrders.size())
          {
              objects = medicineOrders.get(mo);
          }

          if(date.equals(objects[0]))
          {
              count+=(Long)objects[1];
              mo++;
          }

          if(at<ambulanceTrips.size())
          {
              objects = ambulanceTrips.get(at);
          }

          if(date.equals(objects[0]))
          {
              count+=(Long)objects[1];
              at++;
          }

          incomes.add(count*AppConstants.perUserCharge);
      }

      Collections.reverse(dates);

      Collections.reverse(incomes);

      adminStatisticsDTO.setDates(dates);

      adminStatisticsDTO.setIncomes(incomes);

      return  new ResponseEntity<>(adminStatisticsDTO, HttpStatus.OK);
  }

  @GetMapping("/update/user/response/{id}")
  public ResponseEntity<?>updateUserResponse(@PathVariable(name="id")Long id)
  {
      Optional<UserResponse>optionalUserResponse = userResponseRepository.findById(id);

      UserResponse userResponse = optionalUserResponse.get();

      userResponse.setChecked(1);

      userResponseRepository.save(userResponse);

      return new ResponseEntity<>(ApiResponse.create("update","Response is checked"), HttpStatus.OK);
  }

  @GetMapping("user/response/pending")
  public ResponseEntity<?>getAllPendingResponses()
  {
      List<UserResponse>userResponses = userResponseRepository.findByChecked();

      if(userResponses.size()==0)
      {
          return new ResponseEntity<>(ApiResponse.create("empty", "No pending found"),HttpStatus.OK);
      }

      return new ResponseEntity<>(userResponses,HttpStatus.OK);
  }

}
