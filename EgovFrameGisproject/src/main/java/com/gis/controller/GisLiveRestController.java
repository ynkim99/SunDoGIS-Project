package com.gis.controller;

import com.gis.dto.LocalDto;
import com.gis.service.GisLiveService;
import com.gis.vo.Gps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GisLiveRestController {

    private final GisLiveService gisLiveService;


    @PostMapping("/api/live/gps")
    public Gps getLiveGps(@RequestBody LocalDate date) {
        log.info("date = {}", date);
        Gps gps = gisLiveService.findLiveGps(date);
        log.info("gps = {}", gps);

        return gps;
    }

    @PostMapping("/apl/live/route")
    public LocalDto getLiveRoute(@RequestBody LocalDate date) {

        log.info("date = {}", date);
        LocalDto localDto = gisLiveService.findLocalDbOnToday(date);
        log.info("localDto = {}", localDto);

        return localDto;
    }
}
