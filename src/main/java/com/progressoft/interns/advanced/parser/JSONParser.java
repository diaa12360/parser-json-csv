package com.progressoft.interns.advanced.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressoft.interns.advanced.exception.ParserException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JSONParser implements Parser<ArrayList<HashMap<String, String>>> {
    @Override
    public ArrayList<HashMap<String, String>> parse(String filePath) {
        if (filePath == null)
            throw new ParserException("File Path Cannot be null");
        try {
            ObjectMapper om = new ObjectMapper();
            File file = new File(filePath);
            ArrayList<HashMap<String, String>> arr =
                    om.readValue(file, new TypeReference<ArrayList<HashMap<String, String>>>() {
                    });
            if (arr.size() == 0)
                throw new ParserException("File is empty");
            int max = arr.get(0).size();
            // TODO, instead of using the enhanced for loop, you can directly use the old for loop
            int i = 1;
            for (HashMap<String, String> mp : arr) {
                if (mp.size() < max)
                    throw new ParserException("Missing Data on line: " + i);
                i++;
            }
            return arr;
        } catch (IOException e) {
            throw new ParserException("File not found: " + filePath);
        }
    }
}
