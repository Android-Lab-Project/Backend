package com.healthtechbd.backend.service;

import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TimeService timeService;


    public static LocalDate currentDate(String day) {
        DayOfWeek inputDay = DayOfWeek.valueOf(day.toUpperCase());
        LocalDate now = LocalDate.now();
        LocalDate currentWeekDate = now.with(TemporalAdjusters.previousOrSame(inputDay));
        return currentWeekDate;
    }

    public static LocalDate nextDate(String day) {
        DayOfWeek inputDay = DayOfWeek.valueOf(day.toUpperCase());
        LocalDate now = LocalDate.now();
        LocalDate nextWeekDate = now.with(TemporalAdjusters.next(inputDay));
        return nextWeekDate;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public void setSerialTime(Doctor doctor) {


        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {

            Double startTime = doctor.getAvailableTimes().get(i).getStartTime();
            Double endTime = doctor.getAvailableTimes().get(i).getEndTime();

            Double availTime = doctor.getAvailableTimes().get(i).getAvailTime();

            if (doctor.getAvailableTimes().get(i).getDate().equals(LocalDate.now(ZoneId.of("Asia/Dhaka")))) {
                Double currentTime = timeService.convertTimeToDouble(LocalTime.now(ZoneId.of("Asia/Dhaka")));
                if (availTime < currentTime) {
                    availTime = currentTime;
                }
            }

            if ((startTime<endTime && availTime>endTime)||(startTime>endTime && availTime>endTime && availTime<startTime)) {
                doctor.getAvailableTimes().get(i).setAvailTime(-1.0);
            } else {
                doctor.getAvailableTimes().get(i).setAvailTime(availTime);
            }


        }

        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            Double onlineStartTime = doctor.getAvailableOnlineTimes().get(i).getStartTime();
            Double onlineEndTime = doctor.getAvailableOnlineTimes().get(i).getEndTime();

            Double onlineAvailTime = doctor.getAvailableOnlineTimes().get(i).getAvailTime();

            if (doctor.getAvailableOnlineTimes().get(i).getDate().equals(LocalDate.now(ZoneId.of("Asia/Dhaka")))) {
                Double currentTime = timeService.convertTimeToDouble(LocalTime.now(ZoneId.of("Asia/Dhaka")));

                if (onlineAvailTime < currentTime) {
                    onlineAvailTime = currentTime;
                }
            }
            if ((onlineStartTime<onlineEndTime && onlineAvailTime>onlineEndTime)||(onlineStartTime>onlineEndTime && onlineAvailTime>onlineEndTime && onlineAvailTime<onlineStartTime)) {
                doctor.getAvailableOnlineTimes().get(i).setAvailTime(-1.0);
            } else {
                doctor.getAvailableOnlineTimes().get(i).setAvailTime(onlineAvailTime);
            }
        }
    }

    public Double setNewSerialTime(Double time) {
        Integer intTime = time.intValue();

        Double fracTime = time - intTime;

        fracTime += AppConstants.perConsultTime / 100.0;

        if (fracTime >= 0.6) {
            intTime++;
            fracTime -= 0.6;
        }

        return intTime + fracTime;
    }
}
