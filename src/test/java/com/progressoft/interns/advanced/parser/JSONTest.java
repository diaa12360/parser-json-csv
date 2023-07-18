package com.progressoft.interns.advanced.parser;

import com.progressoft.interns.advanced.exception.ParserException;
import com.progressoft.interns.advanced.exception.PropertiesException;
import com.progressoft.interns.advanced.exception.UtilityException;
import com.progressoft.interns.advanced.utility.ParsedDataUtility;
import com.progressoft.interns.advanced.utility.ParsedDataUtilityImpl;
import com.progressoft.interns.advanced.utility.PropertiesUtility;
import com.progressoft.interns.advanced.utility.PropertiesUtilityImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JSONTest {

    // What can you edit
    // 1. ANYCLASSWANTED to the Class you want to use inside your parser
    // 2. the null values of init() method | Example:  parsedDataUtility = null; to  parsedDataUtility = new myImplementationOfUtility();
    // 3. The parserClass inside parser.properties path: test/resources/parser.properties

    private Parser<ArrayList<HashMap<String, String>>> parser;
    private ParsedDataUtility<ArrayList<HashMap<String, String>>> parsedDataUtility;
    private PropertiesUtility propertiesUtility;


    @BeforeEach
    public void init() {
        parsedDataUtility = new ParsedDataUtilityImpl();
        propertiesUtility = new PropertiesUtilityImpl();
        if (propertiesUtility == null) {
            fail("Please initialize propertiesUtility");
        }
        if (parsedDataUtility == null) {
            fail("Please initialize parsedDataUtility");
        }
        propertiesUtility.loadProperties(getFilePath("parser.properties"));
        // TODO, instead of using the same name of the CSV parser, you can use parserJsonClass and add it to the parser.properties
        parser = (Parser<ArrayList<HashMap<String, String>>>) propertiesUtility.getClassFromProperty("parserClass");
    }


    @RepeatedTest(5)
    public void givenValidProperties_whenGetPropertyValueOfWrongProperty_thenThrowPropertiesException() {
        String randomPropertyName = generateRandomString(5);
        PropertiesException propertiesException = assertThrows(PropertiesException.class, () -> propertiesUtility.getPropertyValue(randomPropertyName));
        assertEquals("Property with key: (" + randomPropertyName + ") is not found", propertiesException.getMessage());
    }

    @RepeatedTest(5)
    public void givenInvalidPropertiesPath_whenLoadProperties_thenThrowPropertiesException() {
        String randomPropertyPath = generateRandomString(6) + ".properties";
        PropertiesException propertiesException = assertThrows(PropertiesException.class, () -> propertiesUtility.loadProperties(randomPropertyPath));
        assertEquals("Properties file not found: " + randomPropertyPath, propertiesException.getMessage());
    }

    @Test
    public void givenNullPropertiesPath_whenLoadProperties_thenThrowPropertiesException() {
        PropertiesException propertiesException = assertThrows(PropertiesException.class, () -> propertiesUtility.loadProperties(null));
        assertEquals("File Path Cannot be null", propertiesException.getMessage());
    }

    @RepeatedTest(5)
    public void givenInvalidPath_whenParse_thenThrowParserException() {
        String fakeFileName = generateRandomString(5) + ".json";
        ParserException parserException = assertThrows(ParserException.class, () -> parser.parse(fakeFileName));
        assertEquals("File not found: " + fakeFileName, parserException.getMessage());
    }


    @Test
    public void givenNull_whenParse_thenThrowParserException() {
        ParserException parserException = assertThrows(ParserException.class, () -> parser.parse(null));
        assertEquals("File Path Cannot be null", parserException.getMessage());
    }

    @RepeatedTest(5)
    public void givenValidPathWithMissingData_whenParse_thenThrowParserException() throws IOException {
        Path tempCsv = Files.createTempFile("tempCsv", ".json");
        Random rand = new SecureRandom();
        int number = 0;
        while (number < 1) {
            number = rand.nextInt(101);
        }
        StringBuilder csvValues = new StringBuilder();
        csvValues.append("[\n");
        for (int i = 1; i != number; i++) {
            csvValues.append("  {  \n" +
                    "    \"test\":\"test\",  \n" +
                    "    \"test2\":\"test2\",  \n" +
                    "    \"test3\":\"test3\"  \n" +
                    "  },  ");
        }
        csvValues.append("{ \"Missing on line: ").append(number).append("\": null}");
        csvValues.append("]\n");
        Files.write(tempCsv, csvValues.toString().getBytes());
        ParserException parserException = assertThrows(ParserException.class, () -> parser.parse(tempCsv.toString()));

        assertEquals("Missing Data on line: " + number, parserException.getMessage());
    }

    @Test
    public void givenValidPathButEmpty_whenParse_thenThrowParserException() {
        ParserException parserException = assertThrows(ParserException.class, () -> parser.parse(getFilePath("empty.json")));
        assertEquals("File is empty", parserException.getMessage());
    }

    @RepeatedTest(5)
    public void givenInvalidColumnName_whenAnyUtilityMethod_thenThrowUtilityException() {
        String column = generateRandomString(3) + "Column";
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        UtilityException utilityException = assertThrows(UtilityException.class, () -> parsedDataUtility.getAverageOfColumn(parsedData, column));
        assertEquals("Column " + column + " not found", utilityException.getMessage());

        UtilityException utilityException2 = assertThrows(UtilityException.class, () -> parsedDataUtility.getAverageOfColumn(parsedData, column));
        assertEquals("Column " + column + " not found", utilityException2.getMessage());

        UtilityException utilityException3 = assertThrows(UtilityException.class, () -> parsedDataUtility.getColumnData(parsedData, column));
        assertEquals("Column " + column + " not found", utilityException3.getMessage());
    }

    // TODO Added Tests ----------
    @Test
    public void givenNullColumnName_whenGetColumnData_thenReturnCUtilityException() {
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getColumnData(parsedData, null));
        assertEquals("Column name can not be null", exception.getMessage());
    }

    @Test
    public void givenNullColumnName_whenGetSummation_thenReturnCUtilityException() {
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getSummationOfColumn(parsedData, null));
        assertEquals("Column name can not be null", exception.getMessage());
    }

    @Test
    public void givenNullColumnName_whenGetAverage_thenReturnCUtilityException() {
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getAverageOfColumn(parsedData, null));
        assertEquals("Column name can not be null", exception.getMessage());
    }

    @Test
    public void givenNullParsedData_whenGetAverage_thenThrowUtilityException() {
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getAverageOfColumn(null, "std"));
        assertEquals("Parsed data can not be null", exception.getMessage());
    }

    @Test
    public void givenNullParsedData_whenGetColumnData_thenThrowUtilityException() {
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getColumnData(null, "std"));
        assertEquals("Parsed data can not be null", exception.getMessage());
    }

    @Test
    public void givenNullParsedData_whenGetSummation_thenThrowUtilityException() {
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getSummationOfColumn(null, "std"));
        assertEquals("Parsed data can not be null", exception.getMessage());
    }

    @Test
    public void givenEmptyParsedData_whenGetSummation_thenThrowUtilityException() {
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getSummationOfColumn(new ArrayList<>(), "std"));
        assertEquals("Parsed Data Cannot be Empty", exception.getMessage());
    }

    @Test
    public void givenEmptyParsedData_whenGetAverage_thenThrowUtilityException() {
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getSummationOfColumn(new ArrayList<>(), "std"));
        assertEquals("Parsed Data Cannot be Empty", exception.getMessage());
    }

    @Test
    public void givenEmptyParsedData_whenGetColumnData_thenThrowUtilityException() {
        UtilityException exception = assertThrows(UtilityException.class, () -> parsedDataUtility.getSummationOfColumn(new ArrayList<>(), "std"));
        assertEquals("Parsed Data Cannot be Empty", exception.getMessage());
    }
    // End

    @Test
    public void givenValidProperties_whenGetPropertyValue_thenReturnCorrectValue() {
        assertEquals("testAnswer1", propertiesUtility.getPropertyValue("testProperty"));
        assertEquals("testAnswer2", propertiesUtility.getPropertyValue("testProperty2"));
    }

    @Test
    public void givenValidPath_whenParse_thenReturnCorrectSize() {
        assertEquals(500, parser.parse(getFilePath("sample_1.json")).size());
        assertEquals(941, parser.parse(getFilePath("sample_2.json")).size());
        assertEquals(1000, parser.parse(getFilePath("sample_3.json")).size());
    }

    @Test
    public void givenValidParsedData_whenGetSummation_thenReturnCorrectSummation() {
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        assertEquals(new BigDecimal("24295.00000"), parsedDataUtility.getSummationOfColumn(parsedData, "Student Grade"));
        assertEquals(new BigDecimal("1537.00000"), parsedDataUtility.getSummationOfColumn(parsedData, "Employee years of Experience"));

        ArrayList<HashMap<String, String>> parsedData2 = parser.parse(getFilePath("sample_2.json"));
        assertEquals(new BigDecimal("3711739.00000"), parsedDataUtility.getSummationOfColumn(parsedData2, "Number of international students"));

        ArrayList<HashMap<String, String>> parsedData3 = parser.parse(getFilePath("sample_3.json"));
        assertEquals(new BigDecimal("1008364.00000"), parsedDataUtility.getSummationOfColumn(parsedData3, "Random Numbers One"));
        assertEquals(new BigDecimal("4361384.15000"), parsedDataUtility.getSummationOfColumn(parsedData3, "Random Numbers Six"));
        assertEquals(new BigDecimal("488605.56000"), parsedDataUtility.getSummationOfColumn(parsedData3, "Random Numbers Five"));
    }


    @Test
    public void givenValidParsedData_whenGetAverage_thenReturnCorrectAverage() {
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        assertEquals(new BigDecimal("48.59000"), parsedDataUtility.getAverageOfColumn(parsedData, "Student Grade"));
        assertEquals(new BigDecimal("3.07400"), parsedDataUtility.getAverageOfColumn(parsedData, "Employee years of Experience"));

        ArrayList<HashMap<String, String>> parsedData2 = parser.parse(getFilePath("sample_2.json"));
        assertEquals(new BigDecimal("3944.46227"), parsedDataUtility.getAverageOfColumn(parsedData2, "Number of international students"));

        ArrayList<HashMap<String, String>> parsedData3 = parser.parse(getFilePath("sample_3.json"));
        assertEquals(new BigDecimal("1008.36400"), parsedDataUtility.getAverageOfColumn(parsedData3, "Random Numbers One"));
        assertEquals(new BigDecimal("4361.38415"), parsedDataUtility.getAverageOfColumn(parsedData3, "Random Numbers Six"));
        assertEquals(new BigDecimal("488.60556"), parsedDataUtility.getAverageOfColumn(parsedData3, "Random Numbers Five"));
    }

    @Test
    public void givenValidParsedData_whenGetColumnData_thenReturnCorrectData() {
        ArrayList<HashMap<String, String>> parsedData = parser.parse(getFilePath("sample_1.json"));
        ArrayList<String> expectedColumnData = mockSampleOneData();
        ArrayList<String> actualColumnData = parsedDataUtility.getColumnData(parsedData, "Student Grade");

        assertArrayEquals(expectedColumnData.toArray(new String[0]), actualColumnData.toArray(new String[0]));
    }

    private String getFilePath(String resourcePath) {
        URL resource = this.getClass().getClassLoader().getResource(resourcePath);
        return resource.getPath();
    }


    private ArrayList<String> mockSampleOneData() {
        ArrayList<String> expectedDataColumn = new ArrayList<>();
        expectedDataColumn.add("52.79");
        expectedDataColumn.add("20.11");
        expectedDataColumn.add("21.69");
        expectedDataColumn.add("6.47");
        expectedDataColumn.add("61.91");
        expectedDataColumn.add("13.79");
        expectedDataColumn.add("62.32");
        expectedDataColumn.add("14.68");
        expectedDataColumn.add("97.14");
        expectedDataColumn.add("26.93");
        expectedDataColumn.add("5.51");
        expectedDataColumn.add("6.85");
        expectedDataColumn.add("8.34");
        expectedDataColumn.add("32.23");
        expectedDataColumn.add("42.65");
        expectedDataColumn.add("81.8");
        expectedDataColumn.add("24.32");
        expectedDataColumn.add("9.63");
        expectedDataColumn.add("57.25");
        expectedDataColumn.add("95.45");
        expectedDataColumn.add("24.76");
        expectedDataColumn.add("11.82");
        expectedDataColumn.add("80.98");
        expectedDataColumn.add("50.85");
        expectedDataColumn.add("5.18");
        expectedDataColumn.add("8.35");
        expectedDataColumn.add("27.53");
        expectedDataColumn.add("94.31");
        expectedDataColumn.add("42.37");
        expectedDataColumn.add("66.95");
        expectedDataColumn.add("99.72");
        expectedDataColumn.add("58.56");
        expectedDataColumn.add("64.58");
        expectedDataColumn.add("30.71");
        expectedDataColumn.add("38.97");
        expectedDataColumn.add("82.11");
        expectedDataColumn.add("23.45");
        expectedDataColumn.add("89.39");
        expectedDataColumn.add("98.27");
        expectedDataColumn.add("7.77");
        expectedDataColumn.add("88.28");
        expectedDataColumn.add("85.99");
        expectedDataColumn.add("73.44");
        expectedDataColumn.add("74.47");
        expectedDataColumn.add("46.06");
        expectedDataColumn.add("34.44");
        expectedDataColumn.add("87.43");
        expectedDataColumn.add("4.61");
        expectedDataColumn.add("64.74");
        expectedDataColumn.add("95.01");
        expectedDataColumn.add("64.62");
        expectedDataColumn.add("48.97");
        expectedDataColumn.add("19.62");
        expectedDataColumn.add("67.06");
        expectedDataColumn.add("60.32");
        expectedDataColumn.add("14.38");
        expectedDataColumn.add("90.03");
        expectedDataColumn.add("32.43");
        expectedDataColumn.add("12.64");
        expectedDataColumn.add("35.9");
        expectedDataColumn.add("38.2");
        expectedDataColumn.add("48.33");
        expectedDataColumn.add("56.56");
        expectedDataColumn.add("5.9");
        expectedDataColumn.add("47.56");
        expectedDataColumn.add("60.64");
        expectedDataColumn.add("10.12");
        expectedDataColumn.add("37.86");
        expectedDataColumn.add("86.95");
        expectedDataColumn.add("31.97");
        expectedDataColumn.add("45.84");
        expectedDataColumn.add("77.46");
        expectedDataColumn.add("24.92");
        expectedDataColumn.add("57.94");
        expectedDataColumn.add("1.58");
        expectedDataColumn.add("88.56");
        expectedDataColumn.add("23.08");
        expectedDataColumn.add("33.61");
        expectedDataColumn.add("38.76");
        expectedDataColumn.add("45.01");
        expectedDataColumn.add("86.46");
        expectedDataColumn.add("9.35");
        expectedDataColumn.add("63.1");
        expectedDataColumn.add("11.14");
        expectedDataColumn.add("40.99");
        expectedDataColumn.add("56.78");
        expectedDataColumn.add("15.68");
        expectedDataColumn.add("40.38");
        expectedDataColumn.add("78.71");
        expectedDataColumn.add("29.51");
        expectedDataColumn.add("41.86");
        expectedDataColumn.add("7.99");
        expectedDataColumn.add("17.85");
        expectedDataColumn.add("19.33");
        expectedDataColumn.add("32.6");
        expectedDataColumn.add("91.36");
        expectedDataColumn.add("28.62");
        expectedDataColumn.add("26.34");
        expectedDataColumn.add("68.5");
        expectedDataColumn.add("95.52");
        expectedDataColumn.add("38.66");
        expectedDataColumn.add("22.5");
        expectedDataColumn.add("87.34");
        expectedDataColumn.add("60.02");
        expectedDataColumn.add("57.9");
        expectedDataColumn.add("75.3");
        expectedDataColumn.add("6.18");
        expectedDataColumn.add("99.67");
        expectedDataColumn.add("100.24");
        expectedDataColumn.add("29.73");
        expectedDataColumn.add("40.94");
        expectedDataColumn.add("19.74");
        expectedDataColumn.add("62.91");
        expectedDataColumn.add("32.21");
        expectedDataColumn.add("33.43");
        expectedDataColumn.add("59.22");
        expectedDataColumn.add("70.05");
        expectedDataColumn.add("13.04");
        expectedDataColumn.add("56.75");
        expectedDataColumn.add("43.97");
        expectedDataColumn.add("36.05");
        expectedDataColumn.add("93.55");
        expectedDataColumn.add("61.87");
        expectedDataColumn.add("75.69");
        expectedDataColumn.add("69.44");
        expectedDataColumn.add("76.25");
        expectedDataColumn.add("79.04");
        expectedDataColumn.add("83.43");
        expectedDataColumn.add("78.16");
        expectedDataColumn.add("47.8");
        expectedDataColumn.add("63.51");
        expectedDataColumn.add("12.24");
        expectedDataColumn.add("79.85");
        expectedDataColumn.add("30.37");
        expectedDataColumn.add("88.42");
        expectedDataColumn.add("23.32");
        expectedDataColumn.add("11.58");
        expectedDataColumn.add("94.75");
        expectedDataColumn.add("97.28");
        expectedDataColumn.add("31.6");
        expectedDataColumn.add("32.73");
        expectedDataColumn.add("14.71");
        expectedDataColumn.add("76.28");
        expectedDataColumn.add("30.28");
        expectedDataColumn.add("48.25");
        expectedDataColumn.add("44.43");
        expectedDataColumn.add("90.84");
        expectedDataColumn.add("45.58");
        expectedDataColumn.add("5.9");
        expectedDataColumn.add("65.5");
        expectedDataColumn.add("66.6");
        expectedDataColumn.add("14.37");
        expectedDataColumn.add("20.4");
        expectedDataColumn.add("14.08");
        expectedDataColumn.add("96.34");
        expectedDataColumn.add("6.17");
        expectedDataColumn.add("14.68");
        expectedDataColumn.add("56.21");
        expectedDataColumn.add("39.69");
        expectedDataColumn.add("23.42");
        expectedDataColumn.add("5.18");
        expectedDataColumn.add("77.42");
        expectedDataColumn.add("37.01");
        expectedDataColumn.add("86.03");
        expectedDataColumn.add("19.15");
        expectedDataColumn.add("18.92");
        expectedDataColumn.add("82.59");
        expectedDataColumn.add("41.91");
        expectedDataColumn.add("36.14");
        expectedDataColumn.add("91.9");
        expectedDataColumn.add("67.42");
        expectedDataColumn.add("68.79");
        expectedDataColumn.add("38.43");
        expectedDataColumn.add("19.86");
        expectedDataColumn.add("12.07");
        expectedDataColumn.add("90.32");
        expectedDataColumn.add("51.25");
        expectedDataColumn.add("31.15");
        expectedDataColumn.add("15.19");
        expectedDataColumn.add("15.5");
        expectedDataColumn.add("64.96");
        expectedDataColumn.add("65.89");
        expectedDataColumn.add("14.58");
        expectedDataColumn.add("4.7");
        expectedDataColumn.add("100.03");
        expectedDataColumn.add("73.46");
        expectedDataColumn.add("51.81");
        expectedDataColumn.add("84.77");
        expectedDataColumn.add("61.41");
        expectedDataColumn.add("36.59");
        expectedDataColumn.add("5.68");
        expectedDataColumn.add("18.98");
        expectedDataColumn.add("12.84");
        expectedDataColumn.add("77.1");
        expectedDataColumn.add("41.99");
        expectedDataColumn.add("48.27");
        expectedDataColumn.add("52.95");
        expectedDataColumn.add("74.72");
        expectedDataColumn.add("44.39");
        expectedDataColumn.add("40.57");
        expectedDataColumn.add("47.2");
        expectedDataColumn.add("96.51");
        expectedDataColumn.add("59.62");
        expectedDataColumn.add("9.7");
        expectedDataColumn.add("60.15");
        expectedDataColumn.add("68.74");
        expectedDataColumn.add("61.87");
        expectedDataColumn.add("66.81");
        expectedDataColumn.add("48.87");
        expectedDataColumn.add("4.8");
        expectedDataColumn.add("27.81");
        expectedDataColumn.add("13.92");
        expectedDataColumn.add("32.47");
        expectedDataColumn.add("72.91");
        expectedDataColumn.add("59.91");
        expectedDataColumn.add("28.04");
        expectedDataColumn.add("37.15");
        expectedDataColumn.add("61.9");
        expectedDataColumn.add("91.08");
        expectedDataColumn.add("42.37");
        expectedDataColumn.add("33.14");
        expectedDataColumn.add("49.87");
        expectedDataColumn.add("5.73");
        expectedDataColumn.add("3.67");
        expectedDataColumn.add("58.48");
        expectedDataColumn.add("36.56");
        expectedDataColumn.add("100.15");
        expectedDataColumn.add("67.99");
        expectedDataColumn.add("8.79");
        expectedDataColumn.add("84.51");
        expectedDataColumn.add("52.91");
        expectedDataColumn.add("96.25");
        expectedDataColumn.add("52.31");
        expectedDataColumn.add("27.57");
        expectedDataColumn.add("27.89");
        expectedDataColumn.add("13.56");
        expectedDataColumn.add("7.32");
        expectedDataColumn.add("13.33");
        expectedDataColumn.add("46.23");
        expectedDataColumn.add("80.04");
        expectedDataColumn.add("37.94");
        expectedDataColumn.add("39.02");
        expectedDataColumn.add("100.06");
        expectedDataColumn.add("47.45");
        expectedDataColumn.add("72.21");
        expectedDataColumn.add("19.63");
        expectedDataColumn.add("87.04");
        expectedDataColumn.add("13.1");
        expectedDataColumn.add("28.61");
        expectedDataColumn.add("87.08");
        expectedDataColumn.add("16.47");
        expectedDataColumn.add("29.29");
        expectedDataColumn.add("16.32");
        expectedDataColumn.add("63.52");
        expectedDataColumn.add("56.73");
        expectedDataColumn.add("17.49");
        expectedDataColumn.add("94.69");
        expectedDataColumn.add("92.72");
        expectedDataColumn.add("2.18");
        expectedDataColumn.add("34.21");
        expectedDataColumn.add("51.89");
        expectedDataColumn.add("28.47");
        expectedDataColumn.add("62.43");
        expectedDataColumn.add("16.48");
        expectedDataColumn.add("37.91");
        expectedDataColumn.add("1.48");
        expectedDataColumn.add("96.77");
        expectedDataColumn.add("56.75");
        expectedDataColumn.add("25.49");
        expectedDataColumn.add("64.58");
        expectedDataColumn.add("18.43");
        expectedDataColumn.add("17.21");
        expectedDataColumn.add("83.33");
        expectedDataColumn.add("71.71");
        expectedDataColumn.add("26.94");
        expectedDataColumn.add("86.56");
        expectedDataColumn.add("100.72");
        expectedDataColumn.add("90.22");
        expectedDataColumn.add("96.17");
        expectedDataColumn.add("59.45");
        expectedDataColumn.add("87.4");
        expectedDataColumn.add("2.7");
        expectedDataColumn.add("91.4");
        expectedDataColumn.add("10.55");
        expectedDataColumn.add("72.73");
        expectedDataColumn.add("16.44");
        expectedDataColumn.add("43.67");
        expectedDataColumn.add("21.15");
        expectedDataColumn.add("60.51");
        expectedDataColumn.add("14.45");
        expectedDataColumn.add("94.99");
        expectedDataColumn.add("68.89");
        expectedDataColumn.add("10.91");
        expectedDataColumn.add("43.4");
        expectedDataColumn.add("76.67");
        expectedDataColumn.add("40.31");
        expectedDataColumn.add("57.49");
        expectedDataColumn.add("6.61");
        expectedDataColumn.add("48.15");
        expectedDataColumn.add("64.78");
        expectedDataColumn.add("73.4");
        expectedDataColumn.add("46.56");
        expectedDataColumn.add("21.44");
        expectedDataColumn.add("52.58");
        expectedDataColumn.add("93.77");
        expectedDataColumn.add("90.33");
        expectedDataColumn.add("61.6");
        expectedDataColumn.add("18.69");
        expectedDataColumn.add("26.83");
        expectedDataColumn.add("58.55");
        expectedDataColumn.add("69.27");
        expectedDataColumn.add("23.36");
        expectedDataColumn.add("91.98");
        expectedDataColumn.add("97.88");
        expectedDataColumn.add("51.24");
        expectedDataColumn.add("10.1");
        expectedDataColumn.add("47.61");
        expectedDataColumn.add("68.73");
        expectedDataColumn.add("25.73");
        expectedDataColumn.add("23.86");
        expectedDataColumn.add("65.04");
        expectedDataColumn.add("97.06");
        expectedDataColumn.add("43.11");
        expectedDataColumn.add("14.77");
        expectedDataColumn.add("19.75");
        expectedDataColumn.add("99.64");
        expectedDataColumn.add("84.82");
        expectedDataColumn.add("76.94");
        expectedDataColumn.add("43.96");
        expectedDataColumn.add("22.56");
        expectedDataColumn.add("27.38");
        expectedDataColumn.add("46.86");
        expectedDataColumn.add("80.7");
        expectedDataColumn.add("52.48");
        expectedDataColumn.add("54.16");
        expectedDataColumn.add("67.84");
        expectedDataColumn.add("68.15");
        expectedDataColumn.add("57.87");
        expectedDataColumn.add("38.26");
        expectedDataColumn.add("68.16");
        expectedDataColumn.add("4.89");
        expectedDataColumn.add("94.42");
        expectedDataColumn.add("28.33");
        expectedDataColumn.add("80.26");
        expectedDataColumn.add("99.71");
        expectedDataColumn.add("84.72");
        expectedDataColumn.add("42.16");
        expectedDataColumn.add("77.51");
        expectedDataColumn.add("13.08");
        expectedDataColumn.add("77.83");
        expectedDataColumn.add("66.45");
        expectedDataColumn.add("81.28");
        expectedDataColumn.add("27.53");
        expectedDataColumn.add("4.32");
        expectedDataColumn.add("6.76");
        expectedDataColumn.add("14.3");
        expectedDataColumn.add("77");
        expectedDataColumn.add("85.45");
        expectedDataColumn.add("10.9");
        expectedDataColumn.add("2.19");
        expectedDataColumn.add("38.63");
        expectedDataColumn.add("27.69");
        expectedDataColumn.add("92.19");
        expectedDataColumn.add("33.33");
        expectedDataColumn.add("77.05");
        expectedDataColumn.add("7.19");
        expectedDataColumn.add("53.28");
        expectedDataColumn.add("46.65");
        expectedDataColumn.add("77.44");
        expectedDataColumn.add("99.47");
        expectedDataColumn.add("30.47");
        expectedDataColumn.add("7.81");
        expectedDataColumn.add("25.61");
        expectedDataColumn.add("46.32");
        expectedDataColumn.add("95.19");
        expectedDataColumn.add("24.58");
        expectedDataColumn.add("3.21");
        expectedDataColumn.add("58.98");
        expectedDataColumn.add("38.36");
        expectedDataColumn.add("71.27");
        expectedDataColumn.add("40.23");
        expectedDataColumn.add("58.84");
        expectedDataColumn.add("4.65");
        expectedDataColumn.add("72.52");
        expectedDataColumn.add("9.65");
        expectedDataColumn.add("9.69");
        expectedDataColumn.add("1.5");
        expectedDataColumn.add("75.17");
        expectedDataColumn.add("94.18");
        expectedDataColumn.add("46.55");
        expectedDataColumn.add("83.25");
        expectedDataColumn.add("35.34");
        expectedDataColumn.add("27.02");
        expectedDataColumn.add("22.96");
        expectedDataColumn.add("24.22");
        expectedDataColumn.add("45.11");
        expectedDataColumn.add("55.04");
        expectedDataColumn.add("82.9");
        expectedDataColumn.add("6.8");
        expectedDataColumn.add("66.09");
        expectedDataColumn.add("75.54");
        expectedDataColumn.add("76.8");
        expectedDataColumn.add("31.94");
        expectedDataColumn.add("98.15");
        expectedDataColumn.add("14.4");
        expectedDataColumn.add("58.18");
        expectedDataColumn.add("93.85");
        expectedDataColumn.add("60.37");
        expectedDataColumn.add("4.96");
        expectedDataColumn.add("3.91");
        expectedDataColumn.add("31.71");
        expectedDataColumn.add("24.81");
        expectedDataColumn.add("51.93");
        expectedDataColumn.add("30.03");
        expectedDataColumn.add("60.79");
        expectedDataColumn.add("5.57");
        expectedDataColumn.add("62.82");
        expectedDataColumn.add("38.66");
        expectedDataColumn.add("19.71");
        expectedDataColumn.add("31.37");
        expectedDataColumn.add("99.44");
        expectedDataColumn.add("59.23");
        expectedDataColumn.add("62.43");
        expectedDataColumn.add("78.55");
        expectedDataColumn.add("61.93");
        expectedDataColumn.add("93.19");
        expectedDataColumn.add("29.33");
        expectedDataColumn.add("96.37");
        expectedDataColumn.add("96.58");
        expectedDataColumn.add("29.43");
        expectedDataColumn.add("45.19");
        expectedDataColumn.add("52.26");
        expectedDataColumn.add("85.98");
        expectedDataColumn.add("6.85");
        expectedDataColumn.add("5.73");
        expectedDataColumn.add("94.1");
        expectedDataColumn.add("73.84");
        expectedDataColumn.add("60.82");
        expectedDataColumn.add("50.55");
        expectedDataColumn.add("51.59");
        expectedDataColumn.add("4.3");
        expectedDataColumn.add("45.58");
        expectedDataColumn.add("15.06");
        expectedDataColumn.add("16.3");
        expectedDataColumn.add("17.02");
        expectedDataColumn.add("79.41");
        expectedDataColumn.add("36.22");
        expectedDataColumn.add("62.54");
        expectedDataColumn.add("39.32");
        expectedDataColumn.add("80.96");
        expectedDataColumn.add("57.24");
        expectedDataColumn.add("65.06");
        expectedDataColumn.add("78.86");
        expectedDataColumn.add("59.8");
        expectedDataColumn.add("72.54");
        expectedDataColumn.add("30.9");
        expectedDataColumn.add("30.82");
        expectedDataColumn.add("18.12");
        expectedDataColumn.add("14.74");
        expectedDataColumn.add("51.17");
        expectedDataColumn.add("65.46");
        expectedDataColumn.add("61.54");
        expectedDataColumn.add("5.36");
        expectedDataColumn.add("34.81");
        expectedDataColumn.add("78.65");
        expectedDataColumn.add("13.87");
        expectedDataColumn.add("20.82");
        expectedDataColumn.add("56.79");
        expectedDataColumn.add("37.26");
        expectedDataColumn.add("2.72");
        expectedDataColumn.add("87.22");
        expectedDataColumn.add("83.26");
        expectedDataColumn.add("87.86");
        expectedDataColumn.add("47.26");
        expectedDataColumn.add("83.58");
        expectedDataColumn.add("66.52");
        expectedDataColumn.add("59.67");
        expectedDataColumn.add("74.05");
        expectedDataColumn.add("89.41");
        expectedDataColumn.add("12.2");
        expectedDataColumn.add("34.59");
        expectedDataColumn.add("85.05");
        expectedDataColumn.add("25.31");
        expectedDataColumn.add("36.35");
        expectedDataColumn.add("39.91");
        expectedDataColumn.add("76.48");
        expectedDataColumn.add("23.31");
        expectedDataColumn.add("9.39");
        expectedDataColumn.add("33.89");
        expectedDataColumn.add("10.84");
        expectedDataColumn.add("42.38");
        expectedDataColumn.add("92.38");
        expectedDataColumn.add("7.23");
        expectedDataColumn.add("68.7");
        expectedDataColumn.add("17.77");
        expectedDataColumn.add("49.97");
        expectedDataColumn.add("1.96");
        expectedDataColumn.add("72.87");
        expectedDataColumn.add("96.92");
        expectedDataColumn.add("93.69");
        return expectedDataColumn;
    }

    private static String generateRandomString(int stringLength) {
        return new Random().ints(97, 123)
                .limit(stringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}