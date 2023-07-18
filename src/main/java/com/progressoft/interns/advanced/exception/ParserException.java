package com.progressoft.interns.advanced.exception;

public class ParserException extends RuntimeException {

    // Reference Class of how to make exception.getMessage() work
    public ParserException(String s) {
        super(s);
    }
}
