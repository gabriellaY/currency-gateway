package com.currency.gateway.exception;

public class ExchangeDataNotFoundException extends RuntimeException {

    public ExchangeDataNotFoundException(String message) {
        super(message);
    }
}
