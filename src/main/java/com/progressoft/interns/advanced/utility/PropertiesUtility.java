package com.progressoft.interns.advanced.utility;

import java.util.Properties;

public interface PropertiesUtility {

    void loadProperties(String filePath);
    String getPropertyValue(String propertyName);

    // read about reflection API, and how to create a class from a string
    // the classes will be of type FileUtility and the test cases will use polymorphism
    Object getClassFromProperty(String propertyName);

}
