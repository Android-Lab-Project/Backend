package com.healthtechbd.backend.service;

import com.healthtechbd.backend.entity.Doctor;
import com.healthtechbd.backend.utils.AppConstants;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class DoctorService {

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

    public static void setSerialTime(Doctor doctor) {
        Double serialTime;
        for (int i = 0; i < doctor.getAvailableTimes().size(); i++) {
            Integer startTime = doctor.getAvailableTimes().get(i).getStartTime();
            Integer onlineStartTime = doctor.getAvailableTimes().get(i).getOnlineStartTime();
            Integer endTime = doctor.getAvailableTimes().get(i).getEndTime();
            Integer onlineEndTime = doctor.getAvailableTimes().get(i).getOnlineEndTime();
            Integer count = doctor.getAvailableTimes().get(i).getCount();
            Integer onlineCount = doctor.getAvailableTimes().get(i).getOnlineCount();

            serialTime = startTime * 1.0 + ((count * AppConstants.perConsultTime) / 60) + (((count * AppConstants.perConsultTime) % 60) / 100.0);
            if (serialTime > endTime) {
                doctor.getAvailableTimes().get(i).setAvailTime(0.0);
            } else {
                doctor.getAvailableTimes().get(i).setAvailTime(serialTime);
            }

            serialTime = onlineStartTime * 1.0 + ((onlineCount * AppConstants.perConsultTime) / 60) + (((onlineCount * AppConstants.perConsultTime) % 60) / 100.0);
            if (serialTime > onlineEndTime) {
                doctor.getAvailableTimes().get(i).setOnlineAvailTime(0.0);
            } else {
                doctor.getAvailableTimes().get(i).setOnlineAvailTime(serialTime);
            }
        }
    }


}
