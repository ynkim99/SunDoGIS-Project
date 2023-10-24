package com.gis.mapper;

import com.gis.dto.*;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GisMapper {

    // save Data on Temp Table(DTO)
    void saveGpsOnTempGps(GpsDto gpsDto);
    void saveNoiseOnTempNoise(NoiseDto noiseDto);
    void saveFrequencyOnFrequency(FrequencyDto frequencyDto);


    // find Data By Temp Table(VO)
    List<String> findCarNumByGpsTable();
    Gps findGpsByCarNum(String carNum);
    Noise findNoiseByCarNum(String carNum);
    Frequency findFrequencyByCarNum(String carNum);

    // save localDto on local_db
    void saveSensorData(LocalDto localDto);


    // delete all temp_table data
    void deleteAllTempGps();
    void deleteAllTempNoise();
    void deleteAllTempFrequency();

    // save route
    List<String> findCarNumList();

    List<String> findDateListByCarNum(String carNum);

    List<LocalDto> findLocalDbByCarNumAndDate(CarAndDateDto carAndDateDto);

    List<Gps> findAllGpsByCarNum(String carNum);
    List<Noise> findAllNoiseByCarNum(String carNum);
    List<Frequency> findAllFrequencyByCarNum(String carNum);

    String findDriveTimeByCarNumAndDate(CarAndDateDto carAndDateDto);

    List<Boolean> findAllIsDone(CarAndDateDto carAndDateDto);

    float findDriveDistance(CarAndDateDto carAndDateDto);

    float findPointToPointLength(
            @Param("x1") double x1, @Param("y1") double y1,
            @Param("x2") double x2, @Param("y2") double y2
    );

    CenterPointDto findCenterPoint(CarAndDateDto carAndDateDto);

    String makeCleanLine(@Param("geom1") String geom1, @Param("geom1") String geom2);

    void saveCleanLine(CleanRouteDto cleanRouteDto);

}
