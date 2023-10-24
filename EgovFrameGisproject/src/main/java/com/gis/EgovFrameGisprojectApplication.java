package com.gis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EgovFrameGisprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EgovFrameGisprojectApplication.class, args);
	}

}
