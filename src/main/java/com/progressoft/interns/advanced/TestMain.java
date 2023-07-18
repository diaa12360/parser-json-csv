package com.progressoft.interns.advanced;

import com.progressoft.interns.advanced.parser.CSVParser;
import com.progressoft.interns.advanced.parser.JSONParser;
import com.progressoft.interns.advanced.parser.Parser;
import com.progressoft.interns.advanced.utility.PropertiesUtility;
import com.progressoft.interns.advanced.utility.PropertiesUtilityImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class TestMain {
    public static void main(String[] args) throws IOException {
        JSONParser parser = new JSONParser();
        parser.parse(new TestMain().getFilePath("sample_1.json"));
    }
    private String getFilePath(String resourcePath){
        URL resource = this.getClass().getClassLoader().getResource(resourcePath);
        return resource.getPath();
    }
}
