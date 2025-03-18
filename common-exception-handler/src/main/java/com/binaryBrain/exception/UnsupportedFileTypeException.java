package com.binaryBrain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when file upload contains unsupported file type.
 * Used in file validation process to reject invalid file formats.
 */
@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedFileTypeException extends RuntimeException{
    public UnsupportedFileTypeException(String message){
        super(message);
    }
}
