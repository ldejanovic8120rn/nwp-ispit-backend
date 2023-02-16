package com.raf.nwpdomaci3.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PermissionException extends RuntimeException {

    public PermissionException(String message) {
        super(message);
    }
}
