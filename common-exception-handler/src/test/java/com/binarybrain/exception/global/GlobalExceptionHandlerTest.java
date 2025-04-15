package com.binarybrain.exception.global;

import com.binarybrain.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Mock
    private WebRequest webRequest;

    @Test
    void handleGlobalException() {
        Exception exception = new Exception("Something went wrong!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertNotNull(response.getBody(), "Response body should not be null!");
        assertEquals("Something went wrong!", response.getBody().getMessage());
    }
}