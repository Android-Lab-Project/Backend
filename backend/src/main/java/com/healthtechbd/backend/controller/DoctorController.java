package com.healthtechbd.backend.controller;

import com.healthtechbd.backend.dto.DoctorDTO;
import com.healthtechbd.backend.dto.StatisticsDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.repo.DoctorSerialRepository;
import com.healthtechbd.backend.service.DoctorService;
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
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/doctor/{id}")
    public ResponseEntity<?> showDoctorDetails(@PathVariable Long id) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByAppUser_Id(id);
        Doctor doctor = new Doctor();
        if (optionalDoctor.isPresent()) {
            doctor = optionalDoctor.get();
        } else {
            return new ResponseEntity<>(ApiResponse.create("error", "Doctor not found"), HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {

            if (doctor.getAvailableTimes().get(i).getDate().isBefore(LocalDate.now())) {
                doctor.getAvailableTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                doctor.getAvailableTimes().get(i).setCount(0);
            }
        }


        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            if (doctor.getAvailableOnlineTimes().get(i).getDate().isBefore(LocalDate.now())) {
                doctor.getAvailableOnlineTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                doctor.getAvailableOnlineTimes().get(i).setOnlineCount(0);
            }
        }


        DoctorService.setSerialTime(doctor);

        doctorRepository.save(doctor);

        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);

        doctorDTO.setId(doctor.getAppUser().getId());
        doctorDTO.setFirstName(doctor.getAppUser().getFirstName());
        doctorDTO.setLastName(doctor.getAppUser().getLastName());
        doctorDTO.setEmail(doctor.getAppUser().getEmail());
        doctorDTO.setContactNo(doctor.getAppUser().getContactNo());


        return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
    }

    @GetMapping("/doctor/all")
    public ResponseEntity<?> showAllDoctorDetails() {
        List<Doctor> allDoctors = DoctorService.getAllDoctors();

        if (allDoctors.size() == 0) {
            return new ResponseEntity<>(ApiResponse.create("error", "No Doctor Found"), HttpStatus.BAD_REQUEST);
        }

        for (int it = 0; it < allDoctors.size(); it++) {
            Doctor doctor = allDoctors.get(it);

            for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {

                if (doctor.getAvailableTimes().get(i).getDate().isBefore(LocalDate.now())) {
                    doctor.getAvailableTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                    doctor.getAvailableTimes().get(i).setCount(0);
                }
            }


            for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

                if (doctor.getAvailableOnlineTimes().get(i).getDate().isBefore(LocalDate.now())) {
                    doctor.getAvailableOnlineTimes().get(i).setDate(DoctorService.nextDate(doctor.getAvailableTimes().get(i).getDay()));
                    doctor.getAvailableOnlineTimes().get(i).setOnlineCount(0);
                }
            }


            DoctorService.setSerialTime(doctor);

            doctorRepository.save(doctor);

        }

        List<DoctorDTO> allDoctorsDTO = new ArrayList<>();

        for (int i = 0; i < allDoctors.size(); i++) {
            allDoctorsDTO.add(modelMapper.map(allDoctors.get(i), DoctorDTO.class));
            allDoctorsDTO.get(i).setId(allDoctors.get(i).getAppUser().getId());
            allDoctorsDTO.get(i).setFirstName(allDoctors.get(i).getAppUser().getFirstName());
            allDoctorsDTO.get(i).setLastName(allDoctors.get(i).getAppUser().getLastName());
            allDoctorsDTO.get(i).setEmail(allDoctors.get(i).getAppUser().getEmail());
            allDoctorsDTO.get(i).setContactNo(allDoctors.get(i).getAppUser().getContactNo());
        }
        return new ResponseEntity<>(allDoctorsDTO, HttpStatus.OK);

    }

    @GetMapping("/doctor/statistics")
    public ResponseEntity<?> getHospitalStatistics(HttpServletRequest request) {

        AppUser doctor = userService.returnUser(request);
        if (doctor == null) {
            return new ResponseEntity<>(ApiResponse.create("error", "Hospital not found"), HttpStatus.OK);
        }

        StatisticsDTO doctorStatisticsDTO = new StatisticsDTO();

        doctorStatisticsDTO.set_7DaysCount(doctorSerialRepository.countSerialsByDoctorAndDate(doctor.getId(), LocalDate.now().minusDays(7), LocalDate.now()));

        doctorStatisticsDTO.set_30DaysCount(doctorSerialRepository.countSerialsByDoctorAndDate(doctor.getId(), LocalDate.now().minusDays(30), LocalDate.now()));

        doctorStatisticsDTO.setTotalCount(doctorSerialRepository.countSerialsByDoctor(doctor.getId()));

        doctorStatisticsDTO.set_7DaysIncome(doctorSerialRepository.sumPriceByDoctorAndDate(
                doctor.getId(), LocalDate.now().minusDays(7), LocalDate.now()));


        doctorStatisticsDTO.set_30DaysIncome(doctorSerialRepository.sumPriceByDoctorAndDate(
                doctor.getId(), LocalDate.now().minusDays(30), LocalDate.now()));

        doctorStatisticsDTO.setTotalIncome(doctorSerialRepository.sumPriceByDoctor(doctor.getId()));

        List<Object[]> incomeList = doctorSerialRepository.sumPriceByDoctorAndDateGroupByDate(
                doctor.getId(), LocalDate.now().minusDays(30), LocalDate.now());

        doctorStatisticsDTO.setDates(new ArrayList<>());
        doctorStatisticsDTO.setIncomes(new ArrayList<>());

        for (var i : incomeList) {
            doctorStatisticsDTO.getDates().add((LocalDate) i[0]);
            doctorStatisticsDTO.getIncomes().add((Long) i[1]);
        }

        return new ResponseEntity<>(doctorStatisticsDTO, HttpStatus.OK);
    }


}
