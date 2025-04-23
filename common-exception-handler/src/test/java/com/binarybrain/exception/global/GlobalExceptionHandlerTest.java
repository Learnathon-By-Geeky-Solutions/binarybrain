package com.binarybrain.exception.global;

import com.binarybrain.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

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

    @Test
    void handleGlobalIOException() {
        IOException exception = new IOException("Something went wrong!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleGlobalIOException(exception, webRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertNotNull(response.getBody(), "Response body should not be null!");
        assertEquals("Something went wrong!", response.getBody().getMessage());
    }

    @Test
    void throwIfTest(){
        try{
            GlobalExceptionHandler.Thrower.throwIf(false,new RuntimeException("Something went wrong!"));
        }catch (RuntimeException e){
            fail();
        }
    }

    @Test
    void throwIfTest_False(){
        try{
            GlobalExceptionHandler.Thrower.throwIf(true,new RuntimeException("Something went wrong!"));
        }catch (RuntimeException e){
            assertTrue(true);
        }
    }
}