package com.ennov.tech.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EntityNotFoundException extends RuntimeException {

    private int statusCode;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(int code, String message) {
        super(message);
        setStatusCode(code);
    }
}
