package com.gis.dto;

import lombok.Data;

@Data
public class LocalDto {

    private Long gid;
    private String carNum;
    private String date;
    private String time;
    private Long noise;
    private Long frequency;
    private Double lon;
    private Double lat;
    private String geom;
    private Boolean isDone;

}
