package com.ferapy.exception;

import com.ferapy.dto.error.ErrorDTO;

public class InvalidRequestException extends AbstractErrorException {

    private static final long serialVersionUID = 6660914292827580913L;

    public InvalidRequestException(ErrorDTO errorDTO) {
        super(errorDTO);
    }

}
