package com.progressoft.interns.advanced.parser;

import com.progressoft.interns.advanced.exception.ParserException;
import org.apache.commons.csv.*;

import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CSVParser implements Parser<ArrayList<HashMap<String, String>>> {


    // TODO, private accessor
    ArrayList<HashMap<String, String>> arr = new ArrayList<>();


    // TODO, try to extract the methods into smaller piece of codes, helps with the debugging
    @Override
    public ArrayList<HashMap<String, String>> parse(String filePath) {
        if (filePath == null)
            throw new ParserException("File Path Cannot be null");
        try {
            org.apache.commons.csv.CSVParser parser = new org.apache.commons.csv.CSVParser(new FileReader(filePath), CSVFormat.DEFAULT);
            Iterator<CSVRecord> it = parser.iterator();
            if (!it.hasNext())
                throw new ParserException("File is empty");
            CSVRecord key = it.next();
            arr = new ArrayList<>();
            int line = 1;
            while (it.hasNext()) {
                CSVRecord record = it.next();
                if (record.size() < key.size())
                    throw new ParserException("Missing Data on line: " + (line + 1));
                HashMap<String, String> mp = new HashMap<>();
                for (int i = 0; i < key.size(); i++) {
                    if (record.get(i).matches("\\s*") || record.get(i) == null) {
                        System.out.println(key.get(i) + " " + line);
                        continue;
                    }
                    mp.put(key.get(i), record.get(i));
                }
                line++;
                if(mp.size() == 0) {
                    continue;
                }
                arr.add(mp);
            }
            parser.close();

        } catch (IOException e) {
            throw new ParserException("File not found: " + filePath);
        }
        return arr;
    }

    // TODO, remove unnecessary or unused methods
        @Override
    public String toString() {
        return "I'm a new CSVParser\n";
    }
}
