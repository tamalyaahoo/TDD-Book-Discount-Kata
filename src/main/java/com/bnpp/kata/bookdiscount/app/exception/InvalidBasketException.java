package com.bnpp.kata.bookdiscount.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidBasketException extends RuntimeException{

    private final HttpStatus status;

    public InvalidBasketException(String msg){
        this(msg, HttpStatus.BAD_REQUEST);
    }

    public InvalidBasketException(String msg, HttpStatus status){
        super(msg);
        this.status = status;
    }

}
