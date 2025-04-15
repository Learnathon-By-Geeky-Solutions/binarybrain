package com.binarybrain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserHasNotPermissionExceptionTest {
    @Test
    void testConstructor_setsMessageCorrectly() {
        String errorMessage = "You don't have permission!";
        UserHasNotPermissionException exception = new UserHasNotPermissionException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

}