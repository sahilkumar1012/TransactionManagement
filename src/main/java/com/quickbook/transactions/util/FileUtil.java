package com.quickbook.transactions.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static final String basePath = "src/main/resources";

    public static List<String[]> readCSVFile(String fileName) throws CsvException {
        List<String[]> entries = new ArrayList<>();

        try (CSVReader buyerReader = new CSVReader(new FileReader(basePath + "/" + fileName))) {
            entries = buyerReader.readAll();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to read file : " + fileName + " from path : " + basePath + " " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Unable to read file : " + fileName + " from path : " + basePath + " " + e.getMessage());
            e.printStackTrace();
        }
        return entries;
    }

}
