package com.gis.dto;

import java.util.List;

import lombok.Data;

@Data

public class StatResultDto {

	private Double cleanRatio;
	private List<StatDto> cleanTime;
	private List<StatDto> totalDistance;
	private List<StatDto> noCleanDistance;
	
}
