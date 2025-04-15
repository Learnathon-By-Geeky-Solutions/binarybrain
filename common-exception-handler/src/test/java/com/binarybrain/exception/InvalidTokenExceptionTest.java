package com.binarybrain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidTokenExceptionTest {
    @Test
    void testConstructor_setsMessageCorrectly() {
        String errorMessage = "Invalid token!";
        InvalidTokenException exception = new InvalidTokenException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}