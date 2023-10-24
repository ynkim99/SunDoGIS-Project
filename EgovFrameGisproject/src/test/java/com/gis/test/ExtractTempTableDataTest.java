package com.gis.test;


import com.gis.dto.LocalDto;
import com.gis.service.GisService;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
//@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ExtractTempTableDataTest {

    @Autowired
    private GisService gisService;


    @Test
    @DisplayName("LocalTime Formatting Test")
    void parseTime() {

        String time = "07:44:00.122143";
        String[] split = time.split("\\.");
        String splitTime = split[0];

        LocalTime parse = LocalTime.parse(splitTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
//        LocalTime parse2 = LocalTime.parse("07:44:00", DateTimeFormatter.ofPattern("HH:mm:ss"));
//        LocalTime parse3 = LocalTime.parse("07:44:00.122143", DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"));
        System.out.println("parse = " + parse);

        LocalTime time1 = LocalTime.of(06, 13, 33);
        LocalTime time2 = LocalTime.of(06, 00, 13);
        LocalTime time3 = LocalTime.of(06, 00, 0);

        System.out.println("time1 = " + time1);
        System.out.println("time2 = " + time2);
        System.out.println("time3 = " + time3);

//        System.out.println("parse2 = " + parse2);
//        System.out.println("parse3 = " + parse3);

    }

    @Test
    @DisplayName("TempDTO들중 최근시간만 추출하기")
    void getMostRecentTimeTest(){

        gisService.extractMostRecentTime("07:44:14", "05:44:12", "12:32:57");
    }

    @Test
    @DisplayName("TempDTO -> LocalDto에 넣기")
    void getLocalDto(){

        Gps gps = new Gps("103하2414","2023-10-18", "06:33:01", 127.0695955, 37.29873072);
        Frequency frequency = new Frequency("103하2414","2023-10-18", "06:33:20", 1473L);
        Noise noise = new Noise("103하2414", "2023-10-18", "06:33:33", 107L);

        LocalDto localDto = gisService.saveSensorOnLocalDto(gps, frequency, noise);

        System.out.println("localDto = " + localDto);
    }

}
