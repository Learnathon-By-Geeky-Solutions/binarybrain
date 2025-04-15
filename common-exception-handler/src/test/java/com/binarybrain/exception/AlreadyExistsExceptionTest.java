package com.binarybrain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlreadyExistsExceptionTest {
    @Test
    void testConstructor_setsMessageCorrectly() {
        String errorMessage = "Already exists!";
        AlreadyExistsException exception = new AlreadyExistsException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}