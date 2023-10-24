package com.gis.test;


import com.gis.dto.LocalDto;
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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GisServiceTest {

    @Autowired
    private GisService gisService;

    @Test
    @DisplayName("SensorDto -> LocalDto로 통합하기")
    void combineSensorDto(){

        //given
        List<String> allCarNum = gisService.findAllCarNum();

        //when
        for (String car : allCarNum) {

            Gps gps = gisService.findGps(car);
            Frequency frequency = gisService.findFrequency(car);
            Noise noise = gisService.findNoise(car);

            LocalDto localDto = gisService.saveSensorOnLocalDto(gps, frequency, noise);

            System.out.println("localDto = " + localDto);

            //then
            Assertions.assertThat(localDto.getCarNum()).isEqualTo(car);
            Assertions.assertThat(localDto.getTime()).
                    isEqualTo(gisService.extractMostRecentTime(gps.getTime(), frequency.getTime(), noise.getTime()));
        }
    }

    @Test
    @DisplayName("차량에따른 dateList 가져오기")
    void getDateListByCarNum() {

        List<String> dateList = gisService.findDateListByCarNum("114하6585");
        System.out.println("dateList = " + dateList);
    }

    @Test
    @DisplayName("차량,날짜에 따른 청소비율 구하기")
    void getCleanRatio() {
        List<Boolean> isDoneList = new ArrayList<>();

        int cnt = 0;

        isDoneList.add(false);
        isDoneList.add(false);

        isDoneList.add(true);
        isDoneList.add(true);

        isDoneList.add(false);
        isDoneList.add(false);

        isDoneList.add(true);
        isDoneList.add(false);
        isDoneList.add(true);
        isDoneList.add(false);
        isDoneList.add(false);
        isDoneList.add(false);

        for (int i = 0; i < isDoneList.size()-1; i++) {
            if (!isDoneList.get(i) && !isDoneList.get(i + 1)) {
                cnt += 1;
            }
        }

        int cleanCnt = isDoneList.size() - cnt;
        float ratio = 100 * ((float) cleanCnt / isDoneList.size());
        System.out.println("ratio = " + ratio);

        ratio = Math.round((ratio * 100)) / 100f;


        System.out.println("cnt = " + cnt);
        System.out.println("ratio = " + ratio);

    }

    @Test
    @DisplayName("시간형변환 테스트")
    void formatTime(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

        String time = "8:27:33.128279";
        String time2 = "8:27:33";

        LocalTime localTime = LocalTime.parse(time2, formatter);
        System.out.println("localTime = " + localTime);

        LocalTime fmtTime = LocalTime.of(localTime.getHour(), localTime.getMinute(), localTime.getSecond());
        System.out.println("fmtTime = " + fmtTime);

    }
}
