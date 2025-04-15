package com.binarybrain.exception.global;

import com.binarybrain.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Mock
    private WebRequest webRequest;

    @Test
    void handleUserHasNotPermissionException() {
        UserHasNotPermissionException exception = new UserHasNotPermissionException("User does not have permission");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleUserHasNotPermissionException(exception, webRequest);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("User does not have permission", response.getBody().getMessage());
    }

    @Test
    void handleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Resource not found", response.getBody().getMessage());
    }


    @Test
    void handleUnsupportedFileTypeException() {
        UnsupportedFileTypeException exception = new UnsupportedFileTypeException("Unsupported file type!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleUnsupportedFileTypeException(exception, webRequest);

        assertEquals(415, response.getStatusCodeValue());
        assertEquals("Unsupported file type!", response.getBody().getMessage());
    }

    @Test
    void handleAlreadyExistsException() {
        AlreadyExistsException exception = new AlreadyExistsException("Already exits!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleAlreadyExistsException(exception, webRequest);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Already exits!", response.getBody().getMessage());
    }

    @Test
    void handleInvalidTokenException() {
        InvalidTokenException exception = new InvalidTokenException("Invalid token!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleInvalidTokenException(exception, webRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid token!", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentialsException() {
        BadCredentialsException exception = new BadCredentialsException("Invalid credentials!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleBadCredentialsException(exception, webRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials!", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException() {
        Exception exception = new Exception("Something went wrong!");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Something went wrong!", response.getBody().getMessage());
    }

}