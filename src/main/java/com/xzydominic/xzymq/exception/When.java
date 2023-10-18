package com.xzydominic.xzymq.exception;

import com.xzydominic.xzymq.exception.encapsulation.CommonException;

public class When {

    public When() {}

    public void returnErrorMessage() {

    }

    public void nullReturnErrorMessage(Object value, Integer code, String message, String desc) {
        if (value == null) {
            throw new CommonException(code, message, desc);
        }
    }

}
