package com.ferapy.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
@AllArgsConstructor
public class LocaleInterceptor implements HandlerInterceptor {

    private AcceptHeaderLocaleResolver resolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final Locale locale = resolver.resolveLocale(request);
        LocaleContextHolder.setLocale(locale);
        return true;
    }

}
