package com.gis.service;

import com.gis.dto.*;
import com.gis.mapper.GisMapper;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GisService {

    private final static Long MIN_NOISE = 80L;
    private final static Long MIN_FREQUENCY = 1500L;

    private final static float DIVIDE_HUND = 100f;
    private final static float DIVIDE_THOU = 1000f;

    private final GisMapper gisMapper;

    public void saveTempGps(GpsDto gpsDto) {

        gisMapper.saveGpsOnTempGps(gpsDto);
    }

    public void saveTempNoise(NoiseDto noiseDto) {
        gisMapper.saveNoiseOnTempNoise(noiseDto);
    }

    public void saveTempFrequency(FrequencyDto frequencyDto) {
        gisMapper.saveFrequencyOnFrequency(frequencyDto);
    }

    public void deleteAllTempTable(){

        gisMapper.deleteAllTempGps();
        gisMapper.deleteAllTempNoise();
        gisMapper.deleteAllTempFrequency();

        log.info("========= DELETE ALL TEMP_TABLE DATA =========");
    }

    // gps 좌표에서의 차량정보
    public List<String> findAllCarNum(){
        return gisMapper.findCarNumByGpsTable();
    }

    // loacl_db 에서의 차량정보
    public List<String> findAllCarNumByLocalDb(){

        return gisMapper.findCarNumList();
    }

//     차량에 따른 날짜정보
    public List<String> findDateListByCarNum(String carNum) {

        return gisMapper.findDateListByCarNum(carNum);
    }

    public Gps findGps(String car){
        return gisMapper.findGpsByCarNum(car);
    }

    public Noise findNoise(String car) {
        return gisMapper.findNoiseByCarNum(car);
    }

    public Frequency findFrequency(String car) {
        return gisMapper.findFrequencyByCarNum(car);
    }

    public void saveAllSensor(LocalDto localDto) {
        gisMapper.saveSensorData(localDto);
    }

    public LocalDto saveSensorOnLocalDto(Gps gps, Frequency frequency, Noise noise){

        log.info("call SaveSensorOnLocalDTO----------------------------------");
        String mostRecentTime = extractMostRecentTime(gps.getTime(), frequency.getTime(), noise.getTime());
        LocalDto localDto = new LocalDto();

        localDto.setCarNum(gps.getCarNum());
        localDto.setDate(gps.getDate());
        localDto.setTime(mostRecentTime);
        localDto.setNoise(noise.getNoise());
        localDto.setFrequency(frequency.getFrequency());
        localDto.setLon(gps.getLon());
        localDto.setLat(gps.getLat());
        localDto.setIsDone(isClean(noise.getNoise(), frequency.getFrequency()));

        log.info("localDto = {}", localDto);
        return localDto;
    }

    public String extractMostRecentTime(String gpsTime, String frequencyTime, String noiseTime){

        log.info("gpsTime = {}", gpsTime);

        String[] gpsSplitMils = gpsTime.split("\\.");
        String gpsSplitTimeMils = gpsSplitMils[0];

        String[] noiseSplitMils = noiseTime.split("\\.");
        String noiseSplitTimeMils = noiseSplitMils[0];

        String[] frequencySplitMils = frequencyTime.split("\\.");
        String frequencySplitTimeMils = frequencySplitMils[0];


        String[] GpsSplitTime = gpsSplitTimeMils.split(":");
        int gpsHour = Integer.parseInt(GpsSplitTime[0]);
        int gpsMin = Integer.parseInt(GpsSplitTime[1]);
        int gpsSec = Integer.parseInt(GpsSplitTime[2]);

        String[] noiseSplitTime = noiseSplitTimeMils.split(":");
        int noiseHour = Integer.parseInt(noiseSplitTime[0]);
        int noiseMin = Integer.parseInt(noiseSplitTime[1]);
        int noiseSec = Integer.parseInt(noiseSplitTime[2]);

        String[] frequencySplitTime = frequencySplitTimeMils.split(":");
        int frequencyHour = Integer.parseInt(frequencySplitTime[0]);
        int frequencyMin = Integer.parseInt(frequencySplitTime[1]);
        int frequencySec = Integer.parseInt(frequencySplitTime[2]);

        LocalTime fmtGpsTime = LocalTime.of(gpsHour, gpsMin, gpsSec);
        LocalTime fmtFrequencyTime = LocalTime.of(frequencyHour, frequencyMin, frequencySec);
        LocalTime fmtNoiseTime = LocalTime.of(noiseHour, noiseMin, noiseSec);



        List<LocalTime> timeList = Arrays.asList(fmtGpsTime, fmtFrequencyTime, fmtNoiseTime);
        LocalTime mostRecentTime = Collections.max(timeList);

        log.info("================================================");
        log.info("timeList = {}", timeList);
        log.info("Most Recent Time = [{}]", mostRecentTime);

        return mostRecentTime.toString();
    }

    public Boolean isClean(Long noise, Long frequency){
        // noise >= 80 && frequency >= 1500
        return noise >= MIN_NOISE && frequency >= MIN_FREQUENCY;
    }

    public Map<String, Integer> timeFormatter() {




        return null;
    }

//    진동 소음 검증
//    null값 또는 비정상적으로 들어오는 센서를 검증한다.
    public void validateSensorData(Frequency frequency, Noise noise){

        Long frequencyData = frequency.getFrequency();
        Long noiseData = noise.getNoise();

    }

//    Temp Table 데이터 list로 출력
    public List<Gps> findAllGpsByCarNum(String carNum) {
        return gisMapper.findAllGpsByCarNum(carNum);
    }

    public List<Frequency> findAllFrequencyByCarNum(String carNum) {
        return gisMapper.findAllFrequencyByCarNum(carNum);
    }

    public List<Noise> findAllNoiseByCarNum(String carNum) {
        return gisMapper.findAllNoiseByCarNum(carNum);
    }


    // 운행경로에대한 중심점 구하기
    public CenterPointDto findCenterPoint(CarAndDateDto carAndDateDto) {
        System.out.println("GisService.findCenterPoint");
        log.info("carAndDateDto = {}", carAndDateDto);
        CenterPointDto centerPoint = gisMapper.findCenterPoint(carAndDateDto);

        return centerPoint;
    }

    // 청소 운행 시간
    public String findDriveTime(CarAndDateDto carAndDateDto) {

        return gisMapper.findDriveTimeByCarNumAndDate(carAndDateDto);
    }

    // 청소를 안한 거리
    public double noCleanDistance(CarAndDateDto carAndDateDto) {

        List<LocalDto> locals = gisMapper.findLocalDbByCarNumAndDate(carAndDateDto);
        float length = 0;

        for (int i = 0; i < locals.size()-1; i++) {
            if (!locals.get(i).getIsDone() && !locals.get(i + 1).getIsDone()) {

                length += gisMapper.findPointToPointLength(
                        locals.get(i).getLon(), locals.get(i).getLat(),
                        locals.get(i + 1).getLon(), locals.get(i + 1).getLat()
                );
            }
        }

        return Math.floor(length) / DIVIDE_THOU;
    }

    //총 거리
    public double driveDistance(CarAndDateDto carAndDateDto){

        float driveDistance = gisMapper.findDriveDistance(carAndDateDto);
        //        log.info("distanceKm km = {}", distanceKm);


        return Math.floor(driveDistance) / DIVIDE_THOU;
    }

    //유효 운행거리
    public double cleanDistance(CarAndDateDto carAndDateDto) {

        double calc = driveDistance(carAndDateDto) - noCleanDistance(carAndDateDto);

        double cleanDistance = (Math.floor(calc * DIVIDE_HUND)) / DIVIDE_HUND;

        return cleanDistance;
    }

    // 청소비율
    public int cleanRatio(CarAndDateDto carAndDateDto) {


        double ratio = (cleanDistance(carAndDateDto) / driveDistance(carAndDateDto)) * DIVIDE_HUND;

        return (int) Math.round(ratio);
    }

    public void saveCleanRoute(CarAndDateDto carAndDateDto) {

        List<LocalDto> list = gisMapper.findLocalDbByCarNumAndDate(carAndDateDto);

        for (int i = 0; i < list.size() - 1; i++) {
            if (!list.get(i).getIsDone() && !list.get(i + 1).getIsDone()) {

                CleanRouteDto cleanRouteDto = new CleanRouteDto(list.get(i).getCarNum(), list.get(i).getDate(), list.get(i).getGeom(), list.get(i + 1).getGeom());
                log.info("cleanRouteDto = {}", cleanRouteDto);
                gisMapper.saveCleanLine(cleanRouteDto);

            }
        }

    }

//    Temp Table List -> Local_db에 한번에 저장
    public void combineAllTempTable(List<Gps> gpsList, List<Noise> noiseList, List<Frequency> frequencyList){

            int size = Math.min(Math.min(gpsList.size(), noiseList.size()), frequencyList.size());
            log.info("size = {}", size);
            for (int i = 0; i < size; i++) {
                Gps gps = gpsList.get(i);
                Noise noise = noiseList.get(i);
                Frequency frequency = frequencyList.get(i);

                LocalDto localDto = saveSensorOnLocalDto(gps, frequency, noise);
                saveAllSensor(localDto);
            }
    }

    public List<LocalDto> changeLocalDtoList(List<Gps> gpsList, List<Noise> noiseList, List<Frequency> frequencyList){

        // 반환할 LocalDto List 선언 및 초기화
        List<LocalDto> localList = new ArrayList<>();

        // 센서 데이터 리스트의 인덱스변수 초기화
        int gpsIdx = 0;
        int noiseIdx = 0;
        int frequencyIdx = 0;

        // 병합처리 시작
        while(gpsIdx < gpsList.size() && noiseIdx < noiseList.size() && frequencyIdx < frequencyList.size()) {

            // 현재 인덱스 변수로 센서 데이터를 가져와 병합처리
            LocalDto local = saveSensorOnLocalDto(
                    gpsList.get(gpsIdx),
                    frequencyList.get(frequencyIdx),
                    noiseList.get(noiseIdx));

            // 리스트에 추가
            localList.add(local);

            // 모든 처리가 완료되면 인덱스 증가
            gpsIdx ++;
            noiseIdx ++;
            frequencyIdx ++;
        }

        return localList;
    }

}
