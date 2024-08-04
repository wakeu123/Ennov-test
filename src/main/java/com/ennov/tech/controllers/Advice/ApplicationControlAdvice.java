package com.ennov.tech.controllers.Advice;

import com.ennov.tech.exceptions.ErrorEntity;
import com.ennov.tech.exceptions.ConflictException;
import com.ennov.tech.exceptions.BadRequestException;
import com.ennov.tech.exceptions.EntityNotFoundException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ApplicationControlAdvice {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, NullPointerException.class})
    public @ResponseBody ErrorEntity badRequestHandler(BadRequestException ex) {
        return new ErrorEntity(BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({UsernameNotFoundException.class})
    public @ResponseBody ErrorEntity userNameExceptionHandler(UsernameNotFoundException ex) {
        return new ErrorEntity(NOT_FOUND.value(), ex.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class})
    public @ResponseBody ErrorEntity notFoundRequestHandler(EntityNotFoundException ex) {
        return new ErrorEntity(NOT_FOUND.value(), ex.getMessage());
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public @ResponseBody ErrorEntity conflictRequestHandler(ConflictException ex) {
        return new ErrorEntity(CONFLICT.value(), ex.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody ErrorEntity handlerException(Exception ex) {
        return new ErrorEntity(INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}
