package com.binarybrain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserHasNotPermissionException extends RuntimeException{
    public UserHasNotPermissionException(String message){
        super(message);
    }
}
