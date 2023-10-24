package com.gis.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RouteDto {

    private String carNum;
    private LocalDate date;
    private String geom;
    private Long length;
}
