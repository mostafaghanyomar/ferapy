package com.ferapy.dto.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ferapy.dto.error.constant.ErrorCode;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.ferapy.util.Utils.COMMA_SPACE;
import static com.ferapy.util.Utils.valueOrNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.isEmpty;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDTO {

    @Getter
    @Setter
    @Accessors(chain = true)
    @JsonIgnore
    private String serviceName;
    @Getter
    @Setter
    @Accessors(chain = true)
    private String path;
    @Getter
    @Setter
    @Accessors(chain = true)
    private HttpStatus statusCode;

    @JsonIgnore
    private Set<String> exceptions = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    private Set<String> messages = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    private Set<String> errorCodes = ConcurrentHashMap.newKeySet();

    @JsonProperty
    private final Map<String, Set<ViolationDTO>> violations = new ConcurrentHashMap<>();

    public static ErrorDTO initErrorDTO(String serviceName, String path) {
        return new ErrorDTO()
                .setServiceName(serviceName)
                .setPath(path);
    }

    public static ErrorDTO initErrorDTO(String serviceName) {
        return new ErrorDTO()
                .setServiceName(serviceName);
    }

    public static ErrorDTO initErrorDTO(HttpStatus statusCode) {
        return new ErrorDTO()
                .setStatusCode(statusCode);
    }

    @JsonProperty
    public String getException() {
        return String.join(COMMA_SPACE, exceptions);
    }

    @JsonProperty
    public String getMessage() {
        return String.join(COMMA_SPACE, messages);
    }

    @JsonProperty
    public String getErrorCode() {
        return String.join(COMMA_SPACE, errorCodes);
    }

    public ErrorDTO addException(String exception) {
        if(!isEmpty(exception)) {
            this.exceptions.add(exception);
        }
        return this;
    }

    public ErrorDTO addErrorCode(String errorCode) {
        if(!isEmpty(errorCode)) {
            this.errorCodes.add(errorCode);
        }
        return this;
    }

    public ErrorDTO addErrorCode(ErrorCode errorCode) {
        if(nonNull(errorCode)) {
            this.errorCodes.add(errorCode.name());
        }
        return this;
    }

    public ErrorDTO addMessage(String message) {
        if(!isEmpty(message)) {
            this.messages.add(message);
        }
        return this;
    }

    public ErrorDTO addViolation(String violationId, ErrorCode errorCode) {
        this.violations.computeIfAbsent(violationId, k -> ConcurrentHashMap.newKeySet())
                .add(new ViolationDTO().setMessage(valueOrNull(errorCode, errorCode::getDescription)));
        return this;
    }

    public ErrorDTO addViolation(String violationId, String message) {
        this.violations.computeIfAbsent(violationId, k -> ConcurrentHashMap.newKeySet())
                .add(new ViolationDTO().setMessage(message));
        return this;
    }

    public boolean hasViolations() {
        return !this.violations.isEmpty();
    }

    @Data
    @EqualsAndHashCode
    @Accessors(chain = true)
    public static class ViolationDTO {
        private String message;
    }

}
