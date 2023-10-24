package com.gis.csv;

import com.gis.dto.LocalDto;
import com.gis.vo.Frequency;
import com.gis.vo.Gps;
import com.gis.vo.Noise;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class CsvFile {
    private final String filePath;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm:ss");
    
    //writeAll을 이용한 리스트 데이터 등록
    public void writeAllData(String fileName, List<String[]> dataList) {
        try {
            FileWriter fw = new FileWriter(new File(filePath + "/" + fileName));
            CSVWriter csvWriter = new CSVWriter(fw);
            csvWriter.writeAll(dataList);
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //writeNext를 이용한 리스트 데이터 등록
    public void writeData(String fileName, List<String[]> dataList) {
        try {
            FileWriter fw = new FileWriter(new File(filePath + "/" + fileName));
            CSVWriter csvWriter = new CSVWriter(fw);
            for (String[] data : dataList) {
                csvWriter.writeNext(data);
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // readAll()을 이용한 데이터 읽기
    public List<String[]> readAllCsvData(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(filePath + "/" + fileName);
            CSVReader csvReader = new CSVReader(
	            		new InputStreamReader(fis));
            List<String[]> data = csvReader.readAll();
            csvReader.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (CsvException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // readNext()를 이용한 데이터 읽기
    public List<String[]> readCsvData(String fileName) {
        try {

        	//FileReader fr = new FileReader(new File(filePath + "/" + fileName));
        	//CSVReader csvReader = new CSVReader(fr);
            FileInputStream fis = new FileInputStream(filePath + "/" + fileName);
            CSVReader csvReader = new CSVReader(
            		new InputStreamReader(fis));
            List<String[]> dataList = new ArrayList<>();
            String[] data;
            while ((data = csvReader.readNext()) != null) {
                dataList.add(data);
            }
            csvReader.close();
            return dataList;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // .csv -> List<GpsDto>
    public List<Gps> readGpsCsvData(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(filePath + "/" + fileName);
            CSVReader csvReader = new CSVReader(
            		new InputStreamReader(fis));
            List<Gps> dataList = new ArrayList<>();
            String[] data;
            String carNum = null;
            // 컬럼 이름이 있는 첫줄 넘기기
            csvReader.readNext();            

            //FileWriter fw = new FileWriter(new File(filePath + "/" + "GPS"));
//            FileWriter fw = new FileWriter(new File(filePath + "/" + "GPS.csv"), Charset.forName("UTF-8"));
//            CSVWriter csvWriter = new CSVWriter(fw, ',', CSVWriter.NO_QUOTE_CHARACTER, '`', "\n");
//            String[] first = {"car_num","date","time","lon","lat","noise","frequency"};
//            csvWriter.writeNext(first);
            
            while ((data = csvReader.readNext()) != null) {
            	try {
                	Gps gps = new Gps(
                			data[0],
                			data[1],
                			data[2],
                			// 값이 큰것은 lat, 값이 작은것은 lon
                			Math.max(Double.parseDouble(data[3]),Double.parseDouble(data[4])),
                			Math.min(Double.parseDouble(data[3]),Double.parseDouble(data[4])));
                    dataList.add(gps);
//                	csvWriter.writeNext(new String[]{data[0], data[1], data[2].substring(0,data[2].lastIndexOf(".")), data[3], data[4]});
            	}catch (Exception e) {
            		log.error(e.getMessage());
            		// 오류가 있어도 계속 진행
            		continue;
				}
            	// 차량 이름이 동일한지 비교, 동일하지 않으면 빈 문자열 반환
            	if(carNum == null) {
        			carNum = data[0];
        		}else if(!data[0].equals(carNum)) {
                    csvReader.close();
        			throw new IllegalArgumentException("차량 번호가 일치하지 않음");
        		}
            }
            csvReader.close();
//            csvWriter.flush();
//            csvWriter.close();
            return dataList;
        } catch (IOException | CsvValidationException e) {
        	log.error(e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // .csv -> List<NoiseDto>
    public List<Noise> readNoiseCsvData(String fileName) {
        try {
        	
            FileInputStream fis = new FileInputStream(filePath + "/" + fileName);
            CSVReader csvReader = new CSVReader(
            		new InputStreamReader(fis));
            List<Noise> dataList = new ArrayList<>();
            String[] data;
            String carNum = null;
            // 컬럼 이름이 있는 첫줄 넘기기
            csvReader.readNext();
            

//            FileWriter fw = new FileWriter(new File(filePath + "/" + "NOISE"), Charset.forName("UTF-8"), true);
//            CSVWriter csvWriter = new CSVWriter(fw);
//            String[] first = {"car_num","date","time","lon","lat","noise","frequency"};
//            csvWriter.writeNext(first);
            
            
            while ((data = csvReader.readNext()) != null) {
            	try {
                	Noise noise = new Noise(
                			data[0],
                			data[1],
                			data[2],
                			Long.parseLong(data[3]));
                    dataList.add(noise);
//                	csvWriter.writeNext(new String[]{data[0], data[1], data[2].substring(0,data[2].lastIndexOf(".")), data[3]});
            	}catch (Exception e) {
            		log.error(e.getMessage());
            		// 오류가 있어도 계속 진행
            		continue;
				}
            	// 차량 이름이 동일한지 비교, 동일하지 않으면 빈 문자열 반환
        		if(carNum == null) {
        			carNum = data[0];
        		}else if(!data[0].equals(carNum)) {
                    csvReader.close();
        			throw new IllegalArgumentException("차량 번호가 일치하지 않음");
        		}
            }
            csvReader.close();
//            csvWriter.flush();
//            csvWriter.close();
            return dataList;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // .csv -> List<FrequencyDto>
    public List<Frequency> readFrequencyCsvData(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(filePath + "/" + fileName);
            CSVReader csvReader = new CSVReader(
            		new InputStreamReader(fis));
            List<Frequency> dataList = new ArrayList<>();
            String[] data;
            String carNum = null;
            // 컬럼 이름이 있는 첫줄 넘기기
            csvReader.readNext();            

//            FileWriter fw = new FileWriter(new File(filePath + "/" + "FREQUENCY"));
//            CSVWriter csvWriter = new CSVWriter(fw);
//            String[] first = {"car_num","date","time","lon","lat","noise","frequency"};
//            csvWriter.writeNext(first);
            
            while ((data = csvReader.readNext()) != null) {
            	try {
                	Frequency frequency = new Frequency(
                			data[0],
                			data[1],
                			data[2],
                			Long.valueOf((long) Double.parseDouble(data[3])));
                    dataList.add(frequency);
//                	csvWriter.writeNext(new String[]{data[0], data[1], data[2].substring(0,data[2].lastIndexOf(".")), data[3]});
            	}catch (Exception e) {
            		log.error(e.getMessage());
            		// 이 부분에서는 오류가 있어도 계속 진행
            		continue;
				}
            	// 차량 이름이 동일한지 비교, 동일하지 않으면 빈 문자열 반환
            	if(carNum == null) {
        			carNum = data[0];
        		}else if(!data[0].equals(carNum)) {
                    csvReader.close();
        			throw new IllegalArgumentException("차량 번호가 일치하지 않음");
        		}
            }
            csvReader.close();
            return dataList;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
    // 잘 들어갔는지 테스트 하기 위해 csv로 저장(지워도 됨)
	public void writeLocalData(String fileName, List<LocalDto> dataList) {
        try {

            FileWriter fw = new FileWriter(new File(filePath + "/" + fileName));
            CSVWriter csvWriter = new CSVWriter(fw);
            String[] first = {"car_num","date","time","lon","lat","noise","frequency"};
            csvWriter.writeNext(first);
            for (LocalDto data : dataList) {
                String[] array = {data.getCarNum(), 
                		data.getDate(), 
                		data.getTime(), 
                		String.valueOf(data.getLon()), 
                		String.valueOf(data.getLat()), 
                		String.valueOf(data.getNoise()), 
                		String.valueOf(data.getFrequency())};
            	csvWriter.writeNext(array);
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
    
	
}
