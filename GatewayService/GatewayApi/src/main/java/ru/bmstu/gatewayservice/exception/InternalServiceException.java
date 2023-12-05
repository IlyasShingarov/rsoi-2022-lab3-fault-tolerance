package ru.bmstu.gatewayservice.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InternalServiceException extends RuntimeException{
    public InternalServiceException(String message) {
        super(message);
    }
}
