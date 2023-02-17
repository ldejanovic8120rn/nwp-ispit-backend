package com.raf.nwpispit.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MachineException extends RuntimeException {

    public MachineException(String message) {
        super(message);
    }
}
