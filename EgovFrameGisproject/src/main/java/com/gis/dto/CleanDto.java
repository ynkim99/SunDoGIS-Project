package com.gis.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class CleanDto {

    private String carNum;
    private String date;

    private LocalTime driveTime;
    private int cleanRatio;
    private double driveDistance;
    private double cleanDistance;
    private double noCleanDistance;
    private CenterPointDto centerPointDto;


}
