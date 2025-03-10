package com.onlineclassroom.management.exception.user;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(){
        super("User registration failed: User already exists!");
    }
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
