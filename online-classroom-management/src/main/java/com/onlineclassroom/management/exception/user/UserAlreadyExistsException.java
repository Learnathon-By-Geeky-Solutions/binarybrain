package com.onlineclassroom.management.exception.user;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(){
        super("User registration failed!");
    }
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
