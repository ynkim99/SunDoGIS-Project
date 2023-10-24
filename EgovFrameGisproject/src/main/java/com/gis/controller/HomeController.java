package com.gis.controller;

import com.gis.dto.LocalDto;
import com.gis.dto.OnlyCarNumDto;
import com.gis.dto.UploadCsvDto;
import com.gis.service.GisService;
import com.gis.service.GisStatService;
import com.gis.service.UploadCsvService;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final GisService gisService;
    private final GisStatService gisStatService;
    private final UploadCsvService uploadCsvService;

    @GetMapping(value = {"/map", "/"})
    public String map(Model model) {

        List<String> carNumList = gisService.findAllCarNumByLocalDb();
        model.addAttribute("carNumList", carNumList);

        return "map";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/stat")
    public String stat(Model model) {

        List<OnlyCarNumDto> carList = gisStatService.findCarNum();
        model.addAttribute("localdb", carList);
        log.info("carnum = {}", carList);

        return "stat";
    }

    @GetMapping("/file")
    public String fileInput() {
        return "fileInput";
    }
    // CSV 파일 업로드, DB 저장 처리
    @PostMapping("/file")
    @ResponseBody
    public ResponseEntity<String> saveFile(
            @RequestParam MultipartFile gpsFile,
            @RequestParam MultipartFile noiseFile,
            @RequestParam MultipartFile frequencyFile)
            throws IllegalStateException, IOException {
        log.info("saveFile() 메소드 접근");

        // 1. 파일 업로드 처리
        // 1-1. 파일 객체 변수 선언
        UploadCsvDto gpsCsv = null;
        UploadCsvDto noiseCsv = null;
        UploadCsvDto frequencyCsv = null;

        // 에러가 발생해도 서버에 파일을 지울수 있도록 try-catch-finally 구문 사용
        try {

            // 1-2. 실제 서버에 파일 업로드
            gpsCsv = uploadCsvService.saveCsv(gpsFile);
            noiseCsv = uploadCsvService.saveCsv(noiseFile);
            frequencyCsv = uploadCsvService.saveCsv(frequencyFile);

            // 2. 각 DTO에 담기
            List<Gps> gps = uploadCsvService.readGpsCsv(gpsCsv);
            // (테스트) 리스트가 잘 들어왔나 확인
            log.info("gps.size() : " + gps.size());
            List<Noise> noise = uploadCsvService.readNoiseCsv(noiseCsv);
            log.info("noise.size() : " + noise.size());
            List<Frequency> frequency = uploadCsvService.readFrequencyCsv(frequencyCsv);
            log.info("frequency.size() : " + frequency.size());

            // 3. 차량이름 구하기
            if(!gps.get(0).getCarNum().equals(noise.get(0).getCarNum())
                    || !gps.get(0).getCarNum().equals(frequency.get(0).getCarNum())) {
                // 세 파일의 차량이름이 동일하지 않으면 에러 발생
                log.info("차량 번호가 일치하지 않음");
                log.info("{}, {}, {}", gps.get(0).getCarNum(), noise.get(0).getCarNum(), frequency.get(0).getCarNum());
                throw new IllegalArgumentException("차량 번호가 일치하지 않음");
                // 일치하지 않으면 오류 발생
            }
            log.info("차량 번호가 일치함");
            String carNum = gps.get(0).getCarNum();
            log.info("carNum : " + carNum);

            // 4. LocalDto에 저장
            List<LocalDto> localList = gisService.changeLocalDtoList(gps, noise, frequency);
            // (테스트) 리스트가 잘 만들어졌나 확인
            log.info("localList.size() : " + localList.size());


            // 잘 들어갔는지 테스트 하기 위해 csv로 저장(지워도 됨)
            uploadCsvService.writeLocalData("local_list.csv", localList);

            // 5. 로컬DB에 저장
    		for(LocalDto local : localList) {
        		gisService.saveAllSensor(local);
    		}

        }catch (Exception e) {
            e.getStackTrace();
            throw new IOException("에러발생");
        }finally {
//	    	 6. 파일삭제
            uploadCsvService.deleteCsv(gpsCsv);
            uploadCsvService.deleteCsv(noiseCsv);
            uploadCsvService.deleteCsv(frequencyCsv);
        }
        // 반환값 처리
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("전송 성공");
    }



    @GetMapping("/test")
    @ResponseBody
    public LocalTime test() {
        String str = "08:15:20.123";
        return LocalTime.parse(str);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

//     * (끝) CSV파일 업로드 구현

    /////////////////////////////////////////////////////////////////////////////////////////////



}
