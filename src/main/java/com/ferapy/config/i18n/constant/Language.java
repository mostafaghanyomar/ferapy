package com.ferapy.config.i18n.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * Application Supported languages
 */
@Getter
public enum Language {

    ENGLISH("en"),
    ARABIC("ar");

    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;

    private String code;
    private Locale locale;
    Language(String code) {
        this.code = requireNonNull(code, "Language code is not nullable");
        this.locale = new Locale(code);
    }

    public static Optional<Language> findLanguageByCode(String lang){
        return Arrays.stream(Language.values())
                .filter(language -> language.getCode().equalsIgnoreCase(lang))
                .findFirst();
    }

    public static Optional<Language> findLanguageByLocale(Locale locale){
        if(nonNull(locale)) {
            return Arrays.stream(Language.values())
                    .filter(language -> language.getCode().equalsIgnoreCase(locale.getLanguage()))
                    .findFirst();
        }
        return Optional.empty();
    }

}
