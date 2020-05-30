package com.ferapy.exception.handler;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class ExceptionHandlingController extends BasicErrorController {

    private DefaultExceptionsHandler handler;

    public ExceptionHandlingController(DefaultExceptionsHandler handler,
                                       ErrorAttributes errorAttributes,
                                       ErrorProperties errorProperties,
                                       List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);
        this.handler = handler;
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        return handler.handleException(request);
    }

}
