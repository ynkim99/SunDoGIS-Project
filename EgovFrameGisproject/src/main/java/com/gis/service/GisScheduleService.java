package com.gis.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.gis.dto.LocalDto;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GisScheduleService {

    private final GisService gisService;
    private ThreadPoolTaskScheduler scheduler;
    private String cron = "*/7 * * * * *";

    private static boolean isSchedulerStarted = false;

    public void startScheduler() {

        log.info("================================================");
        log.info("[GisScheduleService] : [startScheduler] : [start]");
        log.info("[JopTime] : {}", getNowDateTime());
        log.info("================================================\n");

        if (!isSchedulerStarted) {

        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(), getTrigger());

        isSchedulerStarted = true;

        }
    }


    public void changeCronSet(String cron) {
        this.cron = cron;
    }

    public void stopScheduler() {

        isSchedulerStarted = false;
        gisService.deleteAllTempTable();
        log.info("================================================");
        log.info("[GisScheduleService] : [callGisService] : [stop]");
        log.info("[JopTime] : {}", getNowDateTime());
        log.info("================================================\n");
        scheduler.shutdown();

        gisService.deleteAllTempTable();

    }

    private Runnable getRunnable() {

        return () -> {
            callGisService();
        };
    }

    private Trigger getTrigger() {

        return new CronTrigger(cron);
    }

//    @PostConstruct
    public void init() {
        startScheduler();
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();

    }

//    public String validationScheduler(String schedulerName) {
//
//        String threadNamePrefix = scheduler.getThreadNamePrefix();
//        if (schedulerName.equals(threadNamePrefix)) {
//            return "ok";
//        }
//
//    }

    public void callGisService() {

        log.info("================================================");
        log.info("[GisScheduleService] : [startScheduler] : [start]");
        log.info("[JopTime] : {}", getNowDateTime());
        log.info("================================================\n");

        List<String> allCarNum = gisService.findAllCarNum();

        for (String car : allCarNum) {

            Gps gps = gisService.findGps(car);
            Noise noise = gisService.findNoise(car);
            Frequency frequency = gisService.findFrequency(car);
            LocalDto localDto = gisService.saveSensorOnLocalDto(gps, frequency, noise);

            gisService.saveAllSensor(localDto);

            log.info("localDTO = {}", localDto);
            log.info("SAVE SENSOR DATA");
            log.info("================================================\n");
        }

//        gisService.deleteAllTempTable();
    }

    public static String getNowDateTime(){

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm:ss E요일");

        return currentDateTime.format(dateTimeFormatter);
    }
}
