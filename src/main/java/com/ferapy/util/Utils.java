package com.ferapy.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static final String COMMA_SPACE = ", ";
    public static final String EMPTY_STRING = "";
    public static final String EQUALS_STRING = "=";
    public static final String GENERIC_INVALID_FIELD_MESSAGE = "Invalid field value!";

    public static <T> T valueOrNull(Object conditionalObj, Supplier<T> valueFactory) {
        return nonNull(conditionalObj) ? valueFactory.get() : null;
    }


    public static String joinNonEmptyStrings(String delimiter, String... strings) {
        StringBuilder builder = new StringBuilder();
        if (ObjectUtils.isEmpty(strings)) {
            return builder.toString();
        }
        for (String str : strings) {
            if (!isEmpty(str)) {
                builder = builder.length() > 0 ?
                        builder.append(delimiter).append(str) :
                        builder.append(str);
            }
        }
        return builder.toString();
    }

    public static HttpStatus resolveHttpStatus(Integer code) {
        try {
            return HttpStatus.valueOf(nonNull(code) && code > 0 ? code : 500);
        } catch (IllegalArgumentException e) {
            log.warn("No matching status code for: {}", code);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
