package com.ferapy.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferapy.config.AppConfig;
import com.ferapy.dto.error.ErrorDTO;
import com.ferapy.exception.AbstractErrorException;
import com.ferapy.exception.ApplicationRuntimeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ferapy.dto.error.constant.ErrorCode.NONSPECIFIC;
import static com.ferapy.exception.AbstractErrorException.UNIDENTIFIED_PATH;
import static com.ferapy.util.Utils.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultExceptionsHandler extends ResponseEntityExceptionHandler {

    private static final Pattern INVALID_FIELDS_FINDER = Pattern.compile("(\\[\")(.*?)(\\\"])");
    private static final String INV_FIELD_WRAP_BEGIN = "[\"";
    private static final String INV_FIELD_WRAP_END = "\"]";

    public static final int MAX_EX_SEARCH_DEPTH_LEVEL = 3;
    public static final String EXCEPTION = "exception";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String PATH = "path";

    private AppConfig appConfig;
    private ObjectMapper mapper;
    private DefaultErrorAttributes errorAttributes;

    @SuppressWarnings("unchecked")
    ResponseEntity<Map<String, Object>> handleException(HttpServletRequest servletRequest) {
        final ServletWebRequest request = new ServletWebRequest(servletRequest);
        final Exception ex = getException(request);
        final ResponseEntity<Object> parentRE = getParentResponseEntity(ex, request);
        final Map<String, Object> errAttr = this.errorAttributes.getErrorAttributes(request, false);
        final ErrorDTO errorDTO = buildErrorDTO(ex, parentRE, errAttr);
        log.error(errorDTO.toString());
        return ResponseEntity
                .status(errorDTO.getStatusCode())
                .headers(getDefaultHeaders(parentRE))
                .body(mapper.convertValue(errorDTO, Map.class));
    }

    private Exception getException(WebRequest request) {
        final Throwable error = errorAttributes.getError(request);
        return error instanceof Exception ? (Exception) error : new Exception(error);
    }

    private ResponseEntity<Object> getParentResponseEntity(Exception ex, WebRequest request) {
        try {
            return super.handleException(ex, request);
        } catch (Exception e) {
            log.debug("The exception is not handled by ResponseEntityExceptionHandler!", e);
            return null;
        }
    }

    private ErrorDTO buildErrorDTO(Exception ex, ResponseEntity<Object> parentRE, Map<String, Object> errAttr) {
        final AbstractErrorException appRuntimeCause = getCause(ex, AbstractErrorException.class);
        ErrorDTO errorDTO = nonNull(appRuntimeCause) ? appRuntimeCause.getErrorDTO() : ErrorDTO.initErrorDTO(appConfig.getAppName());
        if (nonNull(parentRE)) {
            return addErrorDetails(ex, errorDTO.setStatusCode(parentRE.getStatusCode()));
        }
        final ResponseStatusException serverCause = getCause(ex, ResponseStatusException.class);
        if (nonNull(serverCause)) {
            return addErrorDetails(serverCause, errorDTO.setStatusCode(serverCause.getStatus()));
        }
        final WebClientResponseException clientCause = getCause(ex, WebClientResponseException.class);
        if (nonNull(clientCause)) {
            return addErrorDetails(clientCause, errorDTO.setStatusCode(clientCause.getStatusCode()));
        }
        if (nonNull(appRuntimeCause)) {
            return addErrorDetails(appRuntimeCause, errorDTO);
        }
        if (nonNull(errAttr)) {
            return addErrorDetails(ex, errorDTO
                    .setStatusCode(resolveHttpStatus((Integer) errAttr.get(STATUS)))
                    .setPath(isNull(errorDTO.getPath()) ? (String) errAttr.get(PATH) : errorDTO.getPath())
                    .addException((String) errAttr.get(EXCEPTION))
                    .addMessage((String) errAttr.get(ERROR))
                    .addMessage((String) errAttr.get(MESSAGE))
            );
        }
        return addErrorDetails(ex, errorDTO);
    }

    private ErrorDTO addErrorDetails(Throwable ex, ErrorDTO errorDTO) {
        if (isEmpty(errorDTO.getServiceName())) {
            errorDTO.setServiceName(appConfig.getAppName());
        }
        if (isEmpty(errorDTO.getPath())) {
            errorDTO.setPath(UNIDENTIFIED_PATH);
        }
        if (isEmpty(errorDTO.getStatusCode())) {
            errorDTO.setStatusCode(INTERNAL_SERVER_ERROR);
        }
        if (isEmpty(errorDTO.getErrorCode())) {
            errorDTO.addErrorCode(NONSPECIFIC);
        }
        resolveSerializationViolationExceptions(errorDTO, ex);
        errorDTO.addMessage(resolveExceptionMessage(ex, errorDTO));
        return errorDTO.addException(resolveOccurredException(ex));
    }

    private void resolveSerializationViolationExceptions(ErrorDTO errorDTO, Throwable ex) {
        if (nonNull(ex) && !isEmpty(ex.getMessage())) {
            final Matcher matcher = INVALID_FIELDS_FINDER.matcher(ex.getMessage());
            String f;
            while (matcher.find()
                    && !isEmpty(f = matcher.group())
                    && !isEmpty(f = getInvalidFieldName(f))) {
                errorDTO.addViolation(f, GENERIC_INVALID_FIELD_MESSAGE);
            }
        }
    }

    private String getInvalidFieldName(String f) {
        return f.substring(INV_FIELD_WRAP_BEGIN.length(), f.indexOf(INV_FIELD_WRAP_END));
    }

    private String resolveOccurredException(Throwable ex) {
        return nonNull(ex) ? ex.getClass().getSimpleName() : ApplicationRuntimeException.class.getSimpleName();
    }

    private String resolveExceptionMessage(Throwable ex, ErrorDTO errorDTO) {
        return nonNull(ex) && nonNull(ex.getMessage()) ? ex.getMessage() : errorDTO.getStatusCode().getReasonPhrase();
    }

    private HttpHeaders getDefaultHeaders(ResponseEntity<Object> parentRE) {
        final HttpHeaders defaultHeaders = new HttpHeaders();
        return nonNull(parentRE) ? parentRE.getHeaders() : defaultHeaders;
    }

    private <T extends Throwable> T getCause(Throwable ex, Class<T> type) {
        if (nonNull(ex) && type.isInstance(ex)) {
            return type.cast(ex);
        }

        Throwable cause;
        Throwable causeCarrier = ex;
        for (int i = 0; i < MAX_EX_SEARCH_DEPTH_LEVEL && nonNull(causeCarrier); i++) {
            cause = causeCarrier.getCause();
            if (nonNull(cause) && type.isInstance(cause)) {
                return type.cast(cause);
            } else {
                causeCarrier = cause;
            }
        }
        return null;
    }

}
