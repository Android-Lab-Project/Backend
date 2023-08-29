package com.healthtechbd.backend.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class TimeService {

    public LocalTime convertDoubleToTime(double time) {
        int hours = (int) time;
        int minutes = (int) ((time - hours) * 100);

        if (hours >= 12) {
            if (hours > 12) {
                hours -= 12;
            }
            return LocalTime.of(hours, minutes).withHour(12).plusMinutes(minutes).withSecond(0);
        } else {
            return LocalTime.of(hours, minutes).withSecond(0);
        }
    }

    public Double convertTimeToDouble(LocalTime localTime) {
        int hours = localTime.getHour();
        int minutes = localTime.getMinute();

        Double timeInDouble = hours + (minutes / 100.0);

        return timeInDouble;
    }

}
