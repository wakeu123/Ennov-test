package com.ennov.tech.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConflictException extends RuntimeException {

    private int statusCode;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(int code, String message) {
        super(message);
        setStatusCode(code);
    }
}
