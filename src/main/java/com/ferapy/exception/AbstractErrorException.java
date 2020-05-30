package com.ferapy.exception;

import com.ferapy.dto.error.ErrorDTO;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class AbstractErrorException extends NestedRuntimeException {

    private static final long serialVersionUID = 8272980636229803794L;
    public static final String UNIDENTIFIED_PATH = "error";

    private final transient ErrorDTO errorDTO;

    public AbstractErrorException(ErrorDTO errorDTO) {
        super("StatusCode: " +
                requireNonNull(
                        requireNonNull(errorDTO, "ErrorDTO is mandatory for propagating an exception to the consumers in a proper format!")
                                .getStatusCode(), "The errorCode is mandatory for identifying what kind of error has occurred!"
                ).name()
        );
        this.errorDTO = errorDTO;
    }

    public AbstractErrorException(String message, ErrorDTO errorDTO) {
        super(message);
        requireNonNull(
                requireNonNull(errorDTO, "ErrorDTO is mandatory for propagating an exception to the consumers in a proper format!")
                        .getStatusCode(), "The errorCode is mandatory for identifying what kind of error has occurred!"
        );
        this.errorDTO = errorDTO;
    }

}
