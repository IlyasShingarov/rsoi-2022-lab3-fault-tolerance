package ru.bmstu.gatewayservice.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InternalServiceExeption extends RuntimeException{
    public InternalServiceExeption(String message) {
        super(message);
    }
}
