package com.binaryBrain.course.exception;

public class UserHasNotPermissionException extends RuntimeException{
    public UserHasNotPermissionException(String message){
        super(message);
    }
}
