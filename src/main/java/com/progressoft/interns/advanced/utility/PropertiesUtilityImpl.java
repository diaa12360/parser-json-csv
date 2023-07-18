package com.progressoft.interns.advanced.utility;

import com.progressoft.interns.advanced.exception.PropertiesException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtilityImpl implements PropertiesUtility {

    // TODO, rename the p to a better name for the user readability
    // TODO, add private accessor
    Properties p = new Properties();

    @Override
    public void loadProperties(String filePath) {
        if (filePath == null)
            throw new PropertiesException("File Path Cannot be null");
        try (FileReader reader = new FileReader(filePath)) {
            p.load(reader);
        } catch (IOException e) {
            throw new PropertiesException("Properties file not found: " + filePath);
        }
    }

    @Override
    public String getPropertyValue(String propertyName) {
        if (propertyName == null)
            throw new PropertiesException("Property Name can not be null");
        String value = p.getProperty(propertyName);
        if (value == null)
            throw new PropertiesException("Property with key: (" + propertyName + ") is not found");
        return value;
    }

    @Override
    public Object getClassFromProperty(String propertyName) {
        if (propertyName == null)
            throw new PropertiesException("Property Name Cannot be null");
        try {
            return Class.forName(p.getProperty(propertyName)).getConstructor().newInstance();
        } catch (Exception e) {
            throw new PropertiesException("Property Dose not exist");
        }
    }
}
