package com.progressoft.interns.advanced.parser;

import java.io.IOException;

public interface Parser<E> {

    E parse(String filePath);


}
