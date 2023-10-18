package com.xzydominic.xzymq.exception.encapsulation;

public class QueueException extends RabbitException {

    public QueueException() {
        throw new RuntimeException("ZyRabbitQueue: Queue Operations failed");
    }

    public QueueException(String message) {
        throw new RuntimeException("ZyRabbitQueue: Queue Operations failed: " + message);
    }

    public QueueException(String message, Throwable cause) {
        super("ZyRabbitQueue: Queue Operations failed: " + message, cause);
    }

    public QueueException(Throwable cause) {
        super(cause);
    }

}
