package com.gis.dto;

import lombok.Data;

@Data
public class StatDto {

	private String selectedCarNum;
	private int selectedYear;
	private int selectedMonth;

	private String driveTime;
	private Double cleanRatio;
	private Double totalDistance;
	private Double noCleanDistance;

}
