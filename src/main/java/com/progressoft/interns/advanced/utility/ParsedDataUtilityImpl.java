package com.progressoft.interns.advanced.utility;

import com.progressoft.interns.advanced.exception.UtilityException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class ParsedDataUtilityImpl implements ParsedDataUtility<ArrayList<HashMap<String, String>>> {
    @Override
    public BigDecimal getSummationOfColumn(ArrayList<HashMap<String, String>> parsedData, String columnName) {
        if (parsedData == null)
            throw new UtilityException("Parsed data can not be null");
        if (columnName == null)
            throw new UtilityException("Column name can not be null");
        // TODO, Change the .size()  to isEmpty()
        if(parsedData.size() == 0){
            throw new UtilityException("Parsed Data Cannot be Empty");
        }
        if(!parsedData.get(0).containsKey(columnName)){
            throw new UtilityException("Column " + columnName + " not found");
        }
        BigDecimal result = new BigDecimal("0").setScale(5, RoundingMode.HALF_UP);
        // TODO, remove comments, unless you are explaining something difficult for the other developers to understand them
/*
      NOTE : This code is O(n^2)
        for (String s: getColumnData(parsedData, columnName)) {
            BigDecimal value;
            try {
                value = new BigDecimal(s);
            } catch (Exception e) {
                throw new UtilityException("Data is not Numbers");
            }
            result = result.add(value);
        }
*/
        // This Code O(n)
        for (HashMap<String, String> mp : parsedData) {
            BigDecimal value;
            try {
                value = new BigDecimal(mp.get(columnName));
            } catch (Exception e) {
                throw new UtilityException("Data is not Numbers");
            }
            result = result.add(value);
        }
        return result;
    }

    @Override
    public BigDecimal getAverageOfColumn(ArrayList<HashMap<String, String>> parsedData, String columnName) {
        BigDecimal sum = getSummationOfColumn(parsedData, columnName);
        return sum.divide(new BigDecimal(parsedData.size()), RoundingMode.HALF_UP);
    }

    @Override
    public ArrayList<String> getColumnData(ArrayList<HashMap<String, String>> parsedData, String columnName) {
        if (parsedData == null)
            throw new UtilityException("Parsed data can not be null");
        if (columnName == null)
            throw new UtilityException("Column name can not be null");
        if(parsedData.size() == 0){
            throw new UtilityException("Parsed Data Cannot be Empty");
        }
        // TODO, inconsistency of the {} brackets of if statements
        if(!parsedData.get(0).containsKey(columnName)){
            throw new UtilityException("Column " + columnName + " not found");
        }
        ArrayList<String> result = new ArrayList<>();
        for (HashMap<String, String> mp : parsedData) {
            result.add(mp.get(columnName));
        }
        return result;
    }
}
