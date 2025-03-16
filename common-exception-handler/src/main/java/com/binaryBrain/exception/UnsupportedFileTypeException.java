package com.binaryBrain.exception;

/**
 * Exception thrown when file upload contains unsupported file type.
 * Used in file validation process to reject invalid file formats.
 */
public class UnsupportedFileTypeException extends RuntimeException{
    public UnsupportedFileTypeException(String message){
        super(message);
    }
}
