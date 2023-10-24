package com.gis.test;

import com.gis.dto.*;
import com.gis.mapper.GisMapper;
import com.gis.service.GisService;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
//@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GisMapperTest {

    @Autowired
    private GisMapper gisMapper;

    @Autowired
    private GisService gisService;


    @Test
    @DisplayName("TempDto -> TempTable에 저장하기")
    void saveTempDto() {

        gisMapper.saveGpsOnTempGps(new GpsDto("tempCar", "2023-10-18", "07:23:44", 123.44444, 75.12412));
        gisMapper.saveNoiseOnTempNoise(new NoiseDto("tempCar", "2023-10-18", "07:24:44", 54L));
        gisMapper.saveFrequencyOnFrequency(new FrequencyDto("tempCar", "2023-10-18", "07:24:44", 1500L));


    }

    @Test
    @DisplayName("Mapper에서 각 테이블별 VO 테스트")
    void findVO(){

        //given
        List<String> carNumList = gisMapper.findCarNumByGpsTable();
        Gps gps = new Gps();
        Noise noise = new Noise();
        Frequency frequency = new Frequency();

        //when

        for (String car : carNumList) {

            gps = gisMapper.findGpsByCarNum(car);
            noise = gisMapper.findNoiseByCarNum(car);
            frequency = gisMapper.findFrequencyByCarNum(car);

            System.out.println("gps = " + gps);
            System.out.println("noise = " + noise);
            System.out.println("frequency = " + frequency +"\n");
        }

        //then
        Assertions.assertThat(gps.getCarNum()).isEqualTo(noise.getCarNum()).isEqualTo(frequency.getCarNum());
        Assertions.assertThat(gps.getDate()).isEqualTo(noise.getDate()).isEqualTo(frequency.getDate());
    }

    @Test
    @DisplayName("LocalDTO -> insert local_db table 테스트")
    void saveLocalDtoOnLocalDbTable(){

        LocalDto localDto = new LocalDto();

        localDto.setCarNum("103하2414");
        localDto.setDate("2023-06-01");
        localDto.setTime("06:03:20");
        localDto.setNoise(107L);
        localDto.setFrequency(1700L);
        localDto.setLon(127.0695955);
        localDto.setLat(37.29873072);
        localDto.setIsDone(true);

        gisMapper.saveSensorData(localDto);
    }

    @Test
    @DisplayName("차량에 따른 dateList 가져오기")
    void getDateListByCarNum() {

        List<String> dateList = gisMapper.findDateListByCarNum("114하6585");

        System.out.println("dateList = " + dateList);
    }

    @Test
    @DisplayName("localDto -> local_db Table 저장")
    void saveLocalDtoOnTable(){

            List<String> allCarNum = gisService.findAllCarNum();
        for (String car : allCarNum) {

            List<Gps> allGps = gisService.findAllGpsByCarNum(car);
            List<Frequency> allFrequency = gisService.findAllFrequencyByCarNum(car);
            List<Noise> allNoise = gisService.findAllNoiseByCarNum(car);

            gisService.combineAllTempTable(allGps, allNoise , allFrequency);

            
            CarAndDateDto carAndDateDto = new CarAndDateDto();
            carAndDateDto.setCarNum(car);
            carAndDateDto.setDate(allGps.get(0).getDate());
            gisService.saveCleanRoute(carAndDateDto);
        }


            System.out.println("SAVE SENSOR DATA");
            System.out.println("================================================\n");

    }

    @Test
    @DisplayName("날짜, 차량번호에 따른 운행시간 추출하기")
    void findDriveTimeByDateAndCarNum() {
        String date = "2023-08-29";
        String carNum = "103하2414";

        CarAndDateDto carAndDateDto = new CarAndDateDto();
        carAndDateDto.setCarNum(carNum);
        carAndDateDto.setDate(date);

        String driveTime = gisMapper.findDriveTimeByCarNumAndDate(carAndDateDto);

        System.out.println("driveTime = " + driveTime);

    }

    @Test
    @DisplayName("isDone List로 가져오기")
    void findCleanRatio() {

        String date = "2023-08-29";
        String carNum = "103하2414";

        CarAndDateDto carAndDateDto = new CarAndDateDto();
        carAndDateDto.setCarNum(carNum);
        carAndDateDto.setDate(date);

        List<Boolean> allIsDone = gisMapper.findAllIsDone(carAndDateDto);

        System.out.println("allIsDone = " + allIsDone);
    }

    @Test
    @DisplayName("IsDone false,false인 곳만 제외하고 가져오기")
    void findExceptFalseContinuity() {
        String date = "2023-09-07";
        String carNum = "114하6585";

        CarAndDateDto carAndDateDto = new CarAndDateDto();
        carAndDateDto.setCarNum(carNum);
        carAndDateDto.setDate(date);

//        List<Boolean> allIsDone = gisMapper.findAllIsDone(carAndDateDto);
        List<LocalDto> list = gisMapper.findLocalDbByCarNumAndDate(carAndDateDto);

        int count = 0;


        for (int i = 0; i < list.size() - 1; i++) {
            if (!list.get(i).getIsDone() && !list.get(i + 1).getIsDone()) {
                count += 1;
//                String cleanLineString = gisMapper.makeCleanLine(list.get(i).getGeom(), list.get(i + 1).getGeom());
                CleanRouteDto cleanRouteDto = new CleanRouteDto(list.get(i).getCarNum(), list.get(i).getDate(), list.get(i).getGeom(), list.get(i + 1).getGeom());
                System.out.println("cleanRouteDto = " + cleanRouteDto);
                gisMapper.saveCleanLine(cleanRouteDto);
            }
        }

        System.out.println("count = " + count);

        }

    @Test
    void noCleanRoute() {
        
        CarAndDateDto carAndDateDto = new CarAndDateDto();
        carAndDateDto.setCarNum("103하2414");
        carAndDateDto.setDate("2023-08-29");
        gisService.saveCleanRoute(carAndDateDto);

       
        carAndDateDto.setCarNum("103하2414");
        carAndDateDto.setDate("2023-08-30");
        gisService.saveCleanRoute(carAndDateDto);
        
        carAndDateDto.setCarNum("103하2414");
        carAndDateDto.setDate("2023-08-31");
        gisService.saveCleanRoute(carAndDateDto);
    }
    
    @Test
    @DisplayName("점과 점사이 거리 구하기")
    void pointToPointLength() {

        String date = "2023-08-29";
        String carNum = "103하2414";

        CarAndDateDto carAndDateDto = new CarAndDateDto();
        carAndDateDto.setCarNum(carNum);
        carAndDateDto.setDate(date);

        List<LocalDto> locals = gisMapper.findLocalDbByCarNumAndDate(carAndDateDto);

        float length = gisMapper.findPointToPointLength(
                locals.get(0).getLon(), locals.get(0).getLat(),
                locals.get(1).getLon(), locals.get(1).getLat()
                );
        System.out.println("length = " + length);
    }

    @Test
    @DisplayName("운행경로의 중심점 가져오기")
    void findCenterPoint() {

        String date = "2023-08-29";
        String carNum = "103하2414";

        CarAndDateDto carAndDateDto = new CarAndDateDto();
        carAndDateDto.setCarNum(carNum);
        carAndDateDto.setDate(date);

        CenterPointDto centerPoint = gisMapper.findCenterPoint(carAndDateDto);

        System.out.println("centerPoint = " + centerPoint);
    }
}
