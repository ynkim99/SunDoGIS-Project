package com.gis.controller;

import com.gis.dto.CarAndDateDto;
import com.gis.dto.CenterPointDto;
import com.gis.dto.CleanDto;
import com.gis.dto.SensorDto;
import com.gis.service.GisScheduleService;
import com.gis.service.GisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Controller
public class GisRestController {

    private final GisService gisService;
    private final GisScheduleService scheduleService;
    private static final String ONE_MIN = "1";
    private SensorDto sensorDto;

    @PostMapping("/api/sensor/save")
    public String processSensorData(@RequestBody SensorDto sensorData) {

        log.info("sensorData = {}", sensorData);
        sensorDto = sensorData;

        scheduleService.startScheduler();

        // 안드로이드 앱에서 들어오는 sensorData -> temp Table 에 저장
        gisService.saveTempGps(sensorData.getGpsDto());
        gisService.saveTempNoise(sensorData.getNoiseDto());
        gisService.saveTempFrequency(sensorData.getFrequencyDto());

        return "ok";
    }

    @PostMapping("/api/sensor/stop")
    public String sensorRecordStop() {
        String carNum = sensorDto.getGpsDto().getCarNum();
        String date = sensorDto.getGpsDto().getDate();

        CarAndDateDto carAndDateDto = new CarAndDateDto(carNum, date);
        gisService.saveCleanRoute(carAndDateDto);
        scheduleService.stopScheduler();

        return "ok";
    }

    @PostMapping("/api/sensor/cycle")
    public String changeCleanCycle(@RequestBody String minute) {

        log.info("minute = {}", minute);
        String cron;

        scheduleService.stopScheduler();

        if (minute.equals(ONE_MIN)) {
            cron = "0 * * * * *";
        } else {
            cron ="0 */" + minute + " * * * *";
        }

        log.info("cron = {}", cron);
        scheduleService.changeCronSet(cron);
        scheduleService.startScheduler();

        return "ok";

    }

    @PostMapping("/api/dates")
    public List<String> getDateList(@RequestBody String carNum){

        log.info("carNum = {}", carNum);
        List<String> dateList = gisService.findDateListByCarNum(carNum);

        log.info("dateList = {}", dateList);

        return dateList;
    }

    @PostMapping("/api/center-point")
    public CenterPointDto getCenterPoint(@RequestBody CarAndDateDto carAndDateDto) {

        log.info("carAndDateDto = {}", carAndDateDto);
        CenterPointDto centerPoint = gisService.findCenterPoint(carAndDateDto);

        return centerPoint;
    }

    @PostMapping("/api/clean-info")
    public CleanDto findCleanInfo(@RequestBody CarAndDateDto carAndDateDto) {


        CenterPointDto centerPoint = gisService.findCenterPoint(carAndDateDto);

        log.info("carAndDateDto = {}", carAndDateDto);
        String driveTime = gisService.findDriveTime(carAndDateDto);
        double driveDistance = gisService.driveDistance(carAndDateDto);
        double cleanDistance = gisService.cleanDistance(carAndDateDto);
        double noCleanDistance = gisService.noCleanDistance(carAndDateDto);
        int cleanRatio = gisService.cleanRatio(carAndDateDto);

        CleanDto cleanDto = new CleanDto();

        //경로 중심점


        //운행 시간
        log.info("driveTime = {}", driveTime);
        cleanDto.setDriveTime(LocalTime.parse(driveTime));
        //총 거리
        log.info("driveDistance = {}", driveDistance);
        cleanDto.setDriveDistance(driveDistance);
        //청소 거리
        log.info("cleanDistance = {}", cleanDistance);
        cleanDto.setCleanDistance(cleanDistance);
        //청소 비율
        log.info("ratio = {}", cleanRatio);
        cleanDto.setCleanRatio(cleanRatio);
        // 청소를 안한 거리
        log.info("noCleanDistance = {}", noCleanDistance);
        cleanDto.setNoCleanDistance(noCleanDistance);

        return cleanDto;
    }
}
