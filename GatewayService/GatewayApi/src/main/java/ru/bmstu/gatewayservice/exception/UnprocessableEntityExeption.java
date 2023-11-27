package ru.bmstu.gatewayservice.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UnprocessableEntityExeption extends RuntimeException{
    public UnprocessableEntityExeption(String message) {
        super(message);
    }
}
