package com.binaryBrain.classroom_microservice.exception;

public class UserHasNotPermissionException extends RuntimeException{
    public UserHasNotPermissionException(String message){
        super(message);
    }
}
