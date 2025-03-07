package com.binaryBrain.task_microservice.exception;

public class UserHasNotPermissionException extends RuntimeException{
    public UserHasNotPermissionException(String message){
        super(message);
    }
}
