package com.xzydominic.xzymq.exception.encapsulation;

import lombok.Data;

import java.io.Serial;

@Data
@SuppressWarnings("all")
public class CommonException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3743607659429165089L;

    private Integer code;

    private Class<? extends CommonException> type;

    private String message;

    private String description;

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        super(message);
        this.message = message;
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public CommonException(Throwable cause) {
        super(cause);
    }

    public CommonException(Integer code, String message, String description) {
        super(message);
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public CommonException(Integer code, String message, String description, Class<? extends CommonException> type) throws InstantiationException, IllegalAccessException {
        this.code = code;
        this.message = message;
        this.description = description;
        this.type = type;
//        throw new CommonException();
    }

}
