package com.gis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrequencyDto {

    private String carNum;
    private String date;

    private String time;
    private Long frequency;

}
