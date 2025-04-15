package com.binarybrain.exception;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDetailsTest {

    @Test
    void testGettersAndSetters() {
        Date date = new Date();
        String message = "Test message";
        String details = "Test details";

        ErrorDetails errorDetails = new ErrorDetails(date, message, details);

        assertEquals(date, errorDetails.getTimestamp());
        assertEquals(message, errorDetails.getMessage());
        assertEquals(details, errorDetails.getDetails());
    }
}