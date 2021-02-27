package de.erichambuch.biproclient.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public static List<String[]> readCsvFile(InputStream inputStream) throws IOException {
        final List<String[]> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String csvLine;
        while ((csvLine = reader.readLine()) != null) {
            csvLine = csvLine.trim();
            if (csvLine.length() > 1 && !csvLine.startsWith("#")) {
                String[] row = csvLine.split(";");
                resultList.add(row);
            }
        }
        return resultList;
    }
}
