package com.ferapy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferapy.config.AppConfig;
import com.ferapy.dto.error.ErrorDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

import static com.ferapy.dto.error.constant.ErrorCode.SC_UNAUTHORIZED;

@Component
@AllArgsConstructor
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = 467753063607571103L;

    private transient AppConfig appConfig;
    private ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(mapper.writeValueAsString(
                ErrorDTO.initErrorDTO(appConfig.getAppName())
                .setStatusCode(HttpStatus.UNAUTHORIZED)
                .setPath(request.getServletPath())
                .addErrorCode(SC_UNAUTHORIZED)
                .addException(AuthenticationException.class.getSimpleName())
                .addMessage(SC_UNAUTHORIZED.getDescription())));
    }

}