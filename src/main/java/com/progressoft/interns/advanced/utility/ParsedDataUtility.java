package com.progressoft.interns.advanced.utility;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface ParsedDataUtility<E> {


    BigDecimal getSummationOfColumn(E parsedData, String columnName);
    BigDecimal getAverageOfColumn(E parsedData, String columnName);
    ArrayList<String> getColumnData(E parsedData, String columnName);


}
