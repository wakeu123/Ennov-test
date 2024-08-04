package com.ennov.tech.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BadRequestException extends RuntimeException {

    private int statusCode;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(int code, String message) {
        super(message);
        setStatusCode(code);
    }
}

