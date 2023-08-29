package com.healthtechbd.backend.service;

import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.repo.DoctorRepository;
import com.healthtechbd.backend.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class DoctorService {


    private static DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        DoctorService.doctorRepository = doctorRepository;
    }

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

    public static List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public static void setSerialTime(Doctor doctor) {
        Double serialTime;
        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {
            Integer startTime = doctor.getAvailableTimes().get(i).getStartTime();

            Integer endTime = doctor.getAvailableTimes().get(i).getEndTime();

            Integer count = doctor.getAvailableTimes().get(i).getCount();


            serialTime = startTime * 1.0 + ((count * AppConstants.perConsultTime) / 60) + (((count * AppConstants.perConsultTime) % 60) / 100.0);
            if (serialTime > endTime) {
                doctor.getAvailableTimes().get(i).setAvailTime(0.0);
            } else {
                doctor.getAvailableTimes().get(i).setAvailTime(serialTime);
            }


        }

        for (int i = 0; i < doctor.getAvailableOnlineTimes().size(); i++) {

            Integer onlineStartTime = doctor.getAvailableOnlineTimes().get(i).getOnlineStartTime();

            Integer onlineEndTime = doctor.getAvailableOnlineTimes().get(i).getOnlineEndTime();

            Integer onlineCount = doctor.getAvailableOnlineTimes().get(i).getOnlineCount();

            serialTime = onlineStartTime * 1.0 + ((onlineCount * AppConstants.perConsultTime) / 60) + (((onlineCount * AppConstants.perConsultTime) % 60) / 100.0);

            if (serialTime > onlineEndTime) {
                doctor.getAvailableOnlineTimes().get(i).setOnlineAvailTime(0.0);
            } else {
                doctor.getAvailableOnlineTimes().get(i).setOnlineAvailTime(serialTime);
            }
        }
    }


}
