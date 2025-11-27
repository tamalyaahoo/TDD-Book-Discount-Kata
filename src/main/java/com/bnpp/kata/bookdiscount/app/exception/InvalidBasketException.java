package com.bnpp.kata.bookdiscount.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidBasketException extends RuntimeException{

    private final HttpStatus status;

    public InvalidBasketException(String msg){
        this(msg, HttpStatus.BAD_REQUEST);
    }

    public InvalidBasketException(String msg, HttpStatus status){
        super(msg);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
