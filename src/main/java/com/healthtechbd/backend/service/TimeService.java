package com.healthtechbd.backend.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    public  LocalTime convertStringToLocalTime(String timeString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime localTime = LocalTime.parse(timeString, formatter);

        return localTime;
    }

    public ArrayList<String>convertIntToDay(String days)
    {
       String part[]=days.split(",");

       ArrayList<String>dayList = new ArrayList<>();

       for(var i: part)
       {
           if(i.equals("0"))
           {
               dayList.add("Sunday");
           }
           else if(i.equals("1"))
           {
               dayList.add("Monday");
           }
           else if(i.equals("2"))
           {
               dayList.add("Tuesday");
           }
           else if(i.equals("3"))
           {
               dayList.add("Wednesday");
           }
           else if(i.equals("4"))
           {
               dayList.add("Thursday");
           }
           else if(i.equals("5"))
           {
               dayList.add("Friday");
           }
           else if(i.equals("6"))
           {
               dayList.add("Saturday");
           }
       }
       return dayList;
    }

}
