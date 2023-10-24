package com.gis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CleanRouteDto {

    private String carNum;
    private String date;
    private String prevPointGeom;
    private String curPointGeom;
}
