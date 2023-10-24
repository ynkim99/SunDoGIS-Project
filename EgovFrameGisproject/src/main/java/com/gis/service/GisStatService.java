package com.gis.service;

import com.gis.dto.CarAndDateDto;
import com.gis.dto.OnlyCarNumDto;
import com.gis.dto.StatDto;
import com.gis.mapper.GisStatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GisStatService {

    private final GisStatMapper gisStatMapper;

    public List<CarAndDateDto> findLocalDb() {
        return gisStatMapper.findLocalDb();
    };

    public List<OnlyCarNumDto> findCarNum() {
        return gisStatMapper.findCarNum();
    };


    //	통계 페이지 청소 비율
    public Double findRatio(StatDto YearMonthDate) {
        return gisStatMapper.findRatio(YearMonthDate);
    };

    //	통계 페이지 운행 시간
    public List<StatDto> findCleanTime(StatDto yearMonthDate) {
        return gisStatMapper.findCleanTime(yearMonthDate);
    };

    //	통계 페이지 총 운행 거리
    public List<StatDto> findTotalDistance(StatDto yearMonthDate) {

        return gisStatMapper.findTotalDistance(yearMonthDate);
    };

    //	통계 페이지 미청소 거리
    public List<StatDto> findNoCleanDistance(StatDto yearMonthDate){

        return gisStatMapper.findNoCleanDistance(yearMonthDate);
    }

}
