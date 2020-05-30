package com.ferapy.dto.error.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SC_UNAUTHORIZED("Unauthorized Request"),
    BAD_REQUEST("Bad Request"),
    NONSPECIFIC("The occurred error has no specific code");

    private String description;

}
