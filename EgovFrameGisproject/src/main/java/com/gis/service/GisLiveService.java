package com.gis.service;

import com.gis.dto.LocalDto;
import com.gis.mapper.GisLiveMapper;
import com.gis.vo.Gps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GisLiveService {

    private final GisLiveMapper gisLiveMapper;

    public Gps findLiveGps(LocalDate date) {
        return gisLiveMapper.findLiveGpsByTempGps(date);
    }

    public LocalDto findLocalDbOnToday(LocalDate date) {
        return gisLiveMapper.findLocalDbOnToday(date);
    }


}
