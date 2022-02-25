package com.system559.diningout.exception;

public class TokenExpiredException extends Exception {
    public TokenExpiredException(String token) {
        super(String.format("Token %s is expired", token));
    }
}
