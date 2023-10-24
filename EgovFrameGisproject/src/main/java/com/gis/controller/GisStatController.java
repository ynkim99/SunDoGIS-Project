package com.gis.controller;

import com.gis.dto.StatDto;
import com.gis.dto.StatResultDto;
import com.gis.service.GisStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GisStatController {

    private final GisStatService gisStatService;

    @PostMapping("/stat/data")
    @ResponseBody
    public StatResultDto statResultDto(@RequestBody StatDto YearMonthDate, Model model){

        log.info("ajax 요청 데이터 = {}", YearMonthDate);

        Double cleanRatio = gisStatService.findRatio(YearMonthDate);
        List<StatDto> cleanTime = gisStatService.findCleanTime(YearMonthDate);
        List<StatDto> totalDistance = gisStatService.findTotalDistance(YearMonthDate);


        List<StatDto> noCleanDistance = gisStatService.findNoCleanDistance(YearMonthDate);

        model.addAttribute("noCleanDistance", noCleanDistance);


        StatResultDto statResultDto = new StatResultDto();
        statResultDto.setCleanRatio(cleanRatio);
        statResultDto.setCleanTime(cleanTime);
        statResultDto.setTotalDistance(totalDistance);
        statResultDto.setNoCleanDistance(noCleanDistance);

        log.info("statResultDto = {}", statResultDto);
        return statResultDto;
    }
}
