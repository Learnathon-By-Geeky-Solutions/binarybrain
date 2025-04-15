package com.binarybrain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BadCredentialsExceptionTest {
    @Test
    void testConstructor_setsMessageCorrectly() {
        String errorMessage = "Invalid username or password";
        BadCredentialsException exception = new BadCredentialsException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}