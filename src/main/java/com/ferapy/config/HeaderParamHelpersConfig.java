package com.ferapy.config;

import com.ferapy.config.i18n.constant.Language;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class HeaderParamHelpersConfig {

    @Bean
    public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Language.DEFAULT_LANGUAGE.getLocale());
        resolver.setSupportedLocales(Arrays.stream(Language.values())
                .map(Language::getLocale).collect(Collectors.toList()));
        return resolver;
    }

}
