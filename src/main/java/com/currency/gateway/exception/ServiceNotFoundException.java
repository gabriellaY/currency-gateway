package com.currency.gateway.exception;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message) {

        super(message);
    }
}
