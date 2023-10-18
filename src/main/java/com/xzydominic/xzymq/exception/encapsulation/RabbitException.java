package com.xzydominic.xzymq.exception.encapsulation;

public class RabbitException extends CommonException {

    public RabbitException() {
        super();
    }

    public RabbitException(String message) {
        super(message);
    }

    public RabbitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RabbitException(Throwable cause) {
        super(cause);
    }

}
