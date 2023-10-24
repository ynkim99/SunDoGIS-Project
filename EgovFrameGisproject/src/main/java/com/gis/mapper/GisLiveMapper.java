package com.gis.mapper;

import com.gis.dto.LocalDto;
import com.gis.vo.Gps;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface GisLiveMapper {


    Gps findLiveGpsByTempGps(LocalDate date);

    LocalDto findLocalDbOnToday(LocalDate date);

}
