package com.gis.service;

import com.gis.csv.CsvFile;
import com.gis.dto.LocalDto;
import com.gis.dto.UploadCsvDto;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadCsvService {

	/**
	 * 서버에 upload된 파일을 저장할 경로
	 * -> static 폴더 하위에 upload-images 이름의 폴더 필요!
	 * - System.getProperty("user.dir")
	 *   : Java 프로그램에서 현재 프로그램이 실행되는 작업 디렉토리를 나타냄.
	 *   프로그램이 파일을 읽거나 쓸 때 파일 경로를 상대 경로로 지정할 때 중요한 역할
	 */
	private final String rootPath = System.getProperty("user.dir");
	private final String fileDir = rootPath + "/src/main/resources/static/upload-csv/";

    // csv를 저장할 내부 저장소 경로
	private final String csvFilePath = fileDir;
    // csv 객체 초기화
	private final CsvFile csvFile = new CsvFile(csvFilePath);


	public UploadCsvDto saveCsv(MultipartFile file)
			throws IllegalStateException, IOException {

		List<UploadCsvDto> savedCsv = new ArrayList<>();
		//for(MultipartFile multipartFile : files) {
			if (file.isEmpty()) {
				throw new IllegalArgumentException("업로드 된 파일 없음");
			}
			// 원본 파일명; 클라이언트가 업로드한 파일의 원래 파일 이름 반환
			String originalFileName = file.getOriginalFilename();
			log.info("upload file : {}", originalFileName);

			// 원본 파일명 -> 서버에 저장된 파일명 (중복 X)
			// 파일명이 중복되지 않도록 UUID로 설정 + 확장자 유지
			String savedFileName = "Csv" + UUID.randomUUID() + "." + extractExt(originalFileName);

			// 파일 저장 ; multipartFile에 저장된 파일을 서버에 지정된 경로로 저장
			file.transferTo(new File(fileDir + savedFileName));

			// UploadCsv 객체 생성
			UploadCsvDto uploadCsv = UploadCsvDto.builder()
					.originalFileName(originalFileName)
					.savedFileName(savedFileName)
					.build();
			savedCsv.add(uploadCsv);

			log.info(uploadCsv.toString());

		return uploadCsv;
	}

	public List<String[]> readCsv(UploadCsvDto file){
		List<String[]> data = csvFile.readAllCsvData(file.getSavedFileName());
		return data;
	}

	public List<Gps> readGpsCsv(UploadCsvDto file){
		List<Gps> data = csvFile.readGpsCsvData(file.getSavedFileName());
		return data;
	}

	public List<Noise> readNoiseCsv(UploadCsvDto file){
		List<Noise> data = csvFile.readNoiseCsvData(file.getSavedFileName());
		return data;
	}
	public List<Frequency> readFrequencyCsv(UploadCsvDto file){
		List<Frequency> data = csvFile.readFrequencyCsvData(file.getSavedFileName());
		return data;
	}


	 @Transactional
	 public void deleteCsv(UploadCsvDto uploadCsv) throws IOException {
		 log.info("파일 삭제 직전: " + fileDir + uploadCsv.getSavedFileName());
		 try {
			// 파일을 삭제하라!
			 Files.deleteIfExists(
					 Paths.get(fileDir + uploadCsv.getSavedFileName()));
			 log.info("파일 삭제 성공");
		 } catch (IOException e) {
			 log.error("파일 삭제 오류 : " + e.getMessage());
		 }
		 log.info("삭제 시도 후 파일 존재 여부 확인 : " +
				 Files.exists(Paths.get(
						 fileDir + uploadCsv.getSavedFileName())));
	}


	/**
	 * 확장자 추출 메소드
	 */
	private String extractExt(String originalFileName) {
		int pos = originalFileName.lastIndexOf(".");
		return originalFileName.substring(pos + 1);
	}

	public void writeLocalData(String string, List<LocalDto> localList) {
		csvFile.writeLocalData(string, localList);
	}

}
