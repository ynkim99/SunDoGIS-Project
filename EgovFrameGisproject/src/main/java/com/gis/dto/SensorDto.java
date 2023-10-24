package com.gis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SensorDto {

    @JsonProperty("gps")
    private GpsDto gpsDto;

    @JsonProperty("noise")
    private NoiseDto noiseDto;

    @JsonProperty("frequency")
    private FrequencyDto frequencyDto;
}
