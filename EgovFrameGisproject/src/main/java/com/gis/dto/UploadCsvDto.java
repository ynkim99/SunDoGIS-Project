package com.gis.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UploadCsvDto {
	private Long fileId;
	private String originalFileName;
	private String savedFileName;
	private String tempTableName;
}
