package com.gis.test;


import com.gis.dto.FrequencyDto;
import com.gis.dto.GpsDto;
import com.gis.dto.NoiseDto;
import com.gis.dto.SensorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class RequestJsonTest {

    @Test
    @DisplayName("JSON -> DTO 테스트")
    void requestJsonToDto() throws JsonProcessingException {

        // given
        // Java8의 java.time 패키지 지원X
        // mapper에 JavaTimeModule을 추가
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String gps = "{" +
                "\"carNum\" : \"103하2414\", " +
                "\"date\" : \"2023-08-29\", " +
                "\"time\" : \"06:03:01\", " +
                "\"lon\" : \"127.0695955\", " +
                "\"lat\" : \"37.29873072\"" +
                "}";

        String noise = "{" +
                "\"carNum\" : \"103하2414\", " +
                "\"date\" : \"2023-08-29\", " +
                "\"time\" : \"06:03:01\", " +
                "\"noise\" : 107" +
                "}";

        String frequency = "{" +
                "\"carNum\" : \"103하2414\", " +
                "\"date\" : \"2023-08-29\", " +
                "\"time\" : \"06:03:01\", " +
                "\"frequency\" : 1473" +
                "}";

        // when
        GpsDto gpsDto = mapper.readValue(gps, GpsDto.class);
        NoiseDto noiseDto = mapper.readValue(noise, NoiseDto.class);
        FrequencyDto frequencyDto = mapper.readValue(frequency, FrequencyDto.class);

        System.out.println("gpsDto = " + gpsDto);
        System.out.println("noiseDto = " + noiseDto);
        System.out.println("frequencyDto = " + frequencyDto);
    }

    @Test
    @DisplayName("JSON -> SensorDto로 한번에 받기")
    void requestJsonToSensorDto() throws JsonProcessingException {

        // given
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // when
        String sensorData =
            "{" +
                "\"gps\" : {" +
                    "\"carNum\" :\"103하2414\", " +
                    "\"date\" :\"2023-08-29\", " +
                    "\"time\" :\"06:03:01\", " +
                    "\"lon\" :\"127.0695955\", " +
                    "\"lat\" :\"37.29873072\"" +
                    "},"
                +
                "\"noise\" :{" +
                    "\"carNum\" :\"103하2414\", " +
                    "\"date\" :\"2023-08-29\", " +
                    "\"time\" :\"06:03:01\", " +
                    "\"noise\" :107" +
                    "},"
                +
                "\"frequency\" :{" +
                    "\"carNum\" :\"103하2414\", " +
                    "\"date\" :\"2023-08-29\", " +
                    "\"time\" :\"06:03:01\", " +
                    "\"frequency\" :1473" +
                    "}"
                    +
            "}";

        SensorDto sensorDto = mapper.readValue(sensorData, SensorDto.class);
        System.out.println("sensorDto = " + sensorDto);

        // then
        Assertions.assertThat(sensorDto).isNotNull();
        Assertions.assertThat(sensorDto.getFrequencyDto().getFrequency()).isEqualTo(1473);
        Assertions.assertThat(sensorDto.getGpsDto().getLon()).isEqualTo(127.0695955);
        Assertions.assertThat(sensorDto.getGpsDto().getLat()).isEqualTo(37.29873072);

    }
}

