package com.example.gis_project_app;

import android.util.Log;

import com.android.volley.BuildConfig;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFile {
    private final String filePath;

    // 생성자
    public CsvFile(String filePath) {
        this.filePath = filePath;
    }

    //writeAll을 이용한 리스트 데이터 등록
    public void writeAllData(String fileName, List<String[]> dataList) {
        try {
            FileWriter fw = new FileWriter(new File(filePath + "/" + fileName));
            CSVWriter csvWriter = new CSVWriter(fw);
            csvWriter.writeAll(dataList);
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
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
            Log.d("writeData", "filePath: " + filePath + ", fileName : " + fileName);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    // readAll()을 이용한 데이터 읽기
    public List<String[]> readAllCsvData(String fileName) {
        try {
            FileReader fr = new FileReader(new File(filePath + "/" + fileName));
            CSVReader csvReader = new CSVReader(fr);
            List<String[]> data = csvReader.readAll();
            csvReader.close();
            return data;
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        } catch (CsvException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    // readNext()를 이용한 데이터 읽기
    public List<String[]> readCsvData(String fileName) {
        try {
            FileReader fr = new FileReader(new File(filePath + "/" + fileName));
            CSVReader csvReader = new CSVReader(fr);
            List<String[]> dataList = new ArrayList<>();
            String[] data;
            while ((data = csvReader.readNext()) != null) {
                dataList.add(data);
            }
            csvReader.close();
            return dataList;
        } catch (IOException | CsvValidationException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

}
