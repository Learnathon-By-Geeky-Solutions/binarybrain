package com.binarybrain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnsupportedFileTypeExceptionTest {
    @Test
    void testConstructor_setsMessageCorrectly() {
        String errorMessage = "Unsupported file!";
        UnsupportedFileTypeException exception = new UnsupportedFileTypeException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

}