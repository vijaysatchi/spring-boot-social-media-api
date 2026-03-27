package com.example.social_media.exceptions;

public class BadRequestException extends RuntimeException{
    BadRequestException(String message){
        super(message);
    }
}
