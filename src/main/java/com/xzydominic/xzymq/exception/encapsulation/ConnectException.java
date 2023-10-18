package com.xzydominic.xzymq.exception.encapsulation;

public class ConnectException extends RabbitException {

    public ConnectException() {
        throw new RuntimeException("RabbitMQ(ZY) connect failed");
    }

    public ConnectException(String message) {
        throw new RuntimeException("RabbitMQ(ZY) connect failed: " + message);
    }

    public ConnectException(String message, Throwable cause) {
        throw new RuntimeException("RabbitMQ(ZY) connect failed: " + message, cause);
    }

    public ConnectException(Throwable cause) {
        super(cause);
    }

}
