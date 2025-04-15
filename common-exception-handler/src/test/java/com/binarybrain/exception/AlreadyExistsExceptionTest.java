package com.binarybrain.exception;

import com.binarybrain.exception.global.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AlreadyExistsExceptionTest {
    @Mock
    private WebRequest webRequest;
    private final String errorMessage = "Already exists!";
    AlreadyExistsException exception = new AlreadyExistsException(errorMessage);

    @Test
    void testConstructor_setsMessageCorrectly() {
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionHandlingWithGlobalHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorDetails> response = handler.handleAlreadyExistsException(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
    }
}