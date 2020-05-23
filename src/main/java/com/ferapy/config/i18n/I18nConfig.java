package com.ferapy.config.i18n;

import com.ferapy.config.i18n.constant.Language;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class I18nConfig {

    @Bean
    public MessageSource messageSource() {
        DefaultResourceBundleMessageSource messageSource = new DefaultResourceBundleMessageSource();
        messageSource.setDefaultLocale(Language.DEFAULT_LANGUAGE.getLocale());
        return messageSource;
    }

}
