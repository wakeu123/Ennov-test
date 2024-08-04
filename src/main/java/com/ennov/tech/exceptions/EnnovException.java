package com.ennov.tech.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.helpers.MessageFormatter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Setter
@Getter
@SuppressWarnings("serial")
public class EnnovException extends RuntimeException {

    private int statusCode;

    public EnnovException() {
        setStatusCode(INTERNAL_SERVER_ERROR.value());
    }

    public EnnovException(String message) {
        this(INTERNAL_SERVER_ERROR.value(), message);
    }

    public EnnovException(String message, Object... args) {
        this(INTERNAL_SERVER_ERROR.value(), MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public EnnovException(int code, String message) {
        super(message);
        setStatusCode(code);
    }
}
