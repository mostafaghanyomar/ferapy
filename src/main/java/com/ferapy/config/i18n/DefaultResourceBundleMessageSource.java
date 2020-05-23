package com.ferapy.config.i18n;

import com.ferapy.config.i18n.constant.Language;
import com.ferapy.config.i18n.constant.Translations;
import com.ferapy.config.i18n.messagesource.AbstractPropertiesHolderMessageSource;
import com.ferapy.config.i18n.messagesource.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.ferapy.config.i18n.constant.Language.DEFAULT_LANGUAGE;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Slf4j
public class DefaultResourceBundleMessageSource extends AbstractPropertiesHolderMessageSource implements Translator {

    @Override
    public Optional<String> translateTo(String key, Language language) {
        return Optional
                .of(getLanguage(language))
                .map(lang -> getMessage(key, lang.getLocale()));
    }

    @Override
    public Optional<String> translateWithParamsTo(String key, Language language, Object... params) {
        return Optional
                .of(getLanguage(language))
                .map(lang -> getMessage(key, lang.getLocale(), params));
    }

    @Override
    protected Map<Locale, PropertiesHolder> loadCache() {
        Map<Locale, PropertiesHolder> translations = new ConcurrentHashMap<>();
        for (Language language : Language.values()) {
            translations.put(language.getLocale(), getPropertiesHolder(language));
        }
        return translations;
    }

    public void mergeProperties(Language language, Map<String, String> properties) {
        super.mergeProperties(language.getLocale(), new PropertiesHolder(properties, language.getLocale()));
    }

    private Language getLanguage(Language language) {
        return isNull(language) ? DEFAULT_LANGUAGE : language;
    }

    private PropertiesHolder getPropertiesHolder(Language language) {
        final Map<String, String> properties = getProperties(language);
        return new PropertiesHolder(properties, language.getLocale());
    }

    private Map<String, String> getProperties(Language language) {
        final Map<String, String> translations = new ConcurrentHashMap<>();
        List<ResourceBundle> langBundles = Translations.BUNDLES_BASE_NAMES.stream()
                .map(baseName -> getBundle(language, baseName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        Translations.getKeys().forEach(key -> resolveValue(key, langBundles).ifPresent(value -> translations.put(key, value)));
        ResourceBundle.clearCache();
        return translations;
    }

    private Optional<ResourceBundle> getBundle(Language language, String baseName) {
        try {
            return Optional.of(ResourceBundle.getBundle(baseName, language.getLocale()));
        } catch (Exception e) {
            log.error("The bundle is either missing or corrupted!", e);
            return Optional.empty();
        }
    }

    private Optional<String> resolveValue(String translationKey, List<ResourceBundle> bundles) {
        return bundles.stream()
                .filter(bundle -> bundle.containsKey(translationKey))
                .map(bundle -> bundle.getString(translationKey))
                .findFirst();
    }

    private String getMessage(String key, Locale locale, Object... params) {
        try {
            return this.getMessage(key, params, locale);
        } catch (NoSuchMessageException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(MessageFormat.format("No value found for the specified key:{0}, locale:{1}",
                        key, requireNonNull(locale, "Locale can't be null").getLanguage()), e);
            }
            return null;
        }
    }

}

