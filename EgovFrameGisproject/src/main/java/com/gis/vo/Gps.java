package com.gis.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gps {

    private String carNum;
    private String date;
    private String time;
    private Double lon;
    private Double lat;
}
