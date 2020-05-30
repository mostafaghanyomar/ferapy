package com.ferapy.exception;

import com.ferapy.dto.error.ErrorDTO;

public class ApplicationRuntimeException extends AbstractErrorException {

    private static final long serialVersionUID = -7444987108240193873L;

    public ApplicationRuntimeException(ErrorDTO errorDTO) {
        super(errorDTO);
    }
    public ApplicationRuntimeException(String message, ErrorDTO errorDTO) {
        super(message, errorDTO);
    }

}
