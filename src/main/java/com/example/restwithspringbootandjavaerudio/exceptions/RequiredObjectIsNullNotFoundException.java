package com.example.restwithspringbootandjavaerudio.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNullNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RequiredObjectIsNullNotFoundException(String ex) {
        super(ex);
    }

    public RequiredObjectIsNullNotFoundException() {
        super("It's not allowed to persist a null object");
    }
}
