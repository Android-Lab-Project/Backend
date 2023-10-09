package com.healthtechbd.backend.service;

import com.healthtechbd.backend.entity.MedicineReminder;
import com.healthtechbd.backend.repo.MedicineReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
//    private final String MESSAGE_API = "http://66.45.237.70/api.php?username={username}&password={password}&number={number}&message={message}";

    private final String MESSAGE_API = "http://bulksmsbd.net/api/smsapi?api_key=${password}&type=text&number=${number}&senderid={user}&message=${message}";
    @Autowired
    MedicineReminderRepository medicineReminderRepository;
    @Autowired
    TimeService timeService;
    @Value("${message.service.user}")
    private String username;
    @Value("${message.service.password}")
    private String password;

    @Scheduled(cron = "0 * * * * *")
    public void sendMedicineReminders() {
        logger.info("Reminder Schedular started");

        int coreCount = Runtime.getRuntime().availableProcessors();

        logger.info("Running on available " + coreCount + " cores");

        ExecutorService executorService = Executors.newFixedThreadPool(coreCount);

        List<MedicineReminder> medicineReminders = medicineReminderRepository.findAll();

        for (var medicineReminder : medicineReminders) {
            executorService.execute(() -> {
                sendMedicineReminder(medicineReminder);
            });
        }

        logger.info("Reminder Schedular finished");
    }

    public void sendMedicineReminder(MedicineReminder medicineReminder) {
        LocalTime time = timeService.convertStringToLocalTime(medicineReminder.getTime());
        ArrayList<String> days = timeService.convertIntToDay(medicineReminder.getDays());
        LocalTime currentTime = LocalTime.parse(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                DateTimeFormatter.ofPattern("HH:mm"));

        for (var i : days) {
            if (time.equals(currentTime) && i.equalsIgnoreCase(LocalDate.now().getDayOfWeek().toString())) {
                if (!sendMessage(medicineReminder.getDescription(), medicineReminder.getTime(), medicineReminder.getAppUser().getContactNo())) {
                    logger.info("Error sending messages");
                }
            }
        }
    }

    public boolean sendMessage(String medicine, String time, String contactNo) {
        String message = "You have to take " + medicine + " at " + time + ". Kindly take it";
        String url = MESSAGE_API.replace("{username}", username).replace("{password}", password).replace("{number}", contactNo).replace("{message}", message);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Reminder is successfully sent");
            return true;
        } else {
            logger.info("Failed to send reminder. HTTP status code: " + response.getStatusCodeValue());
            return false;
        }

    }
}
