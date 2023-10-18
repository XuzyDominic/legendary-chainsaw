package com.xzydominic.xzymq.exception.encapsulation;

public class ExchangeException extends RabbitException {

    public ExchangeException() {
        throw new RuntimeException("ZyExchange: Operations failed");
    }

    public ExchangeException(String message) {
        throw new RuntimeException("ZyExchange: Operations failed: " + message);
    }

    public ExchangeException(String message, Throwable cause) {
        throw new RuntimeException("ZyExchange: Operations failed: " + message, cause);
    }

    public ExchangeException(Throwable cause) {
        super(cause);
    }
}
