package com.ferapy.config.i18n.messagesource;

import org.springframework.context.support.AbstractMessageSource;
import org.springframework.lang.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.*;

public abstract class AbstractPropertiesHolderMessageSource extends AbstractMessageSource {

    private final Map<Locale, PropertiesHolder> cachedProperties;
    @Nullable
    private Locale defaultLocale;

    public AbstractPropertiesHolderMessageSource() {
        Map<Locale, PropertiesHolder> localeProperties = loadCache();
        if (isNull(localeProperties) || localeProperties.values().stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("Null Values are not allowed");
        }
        cachedProperties = localeProperties;
    }

    @Override
    public String resolveCodeWithoutArguments(String code, Locale locale) {
        String property = this.getPropertiesHolder(locale)
                .map(propHolder -> propHolder.getProperty(code))
                .orElse(null);
        if (isNull(property) && nonNull(defaultLocale) && !defaultLocale.equals(locale)) {
            property = this.getPropertiesHolder(defaultLocale)
                    .map(propHolder -> propHolder.getProperty(code))
                    .orElse(null);
        }
        return property;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        MessageFormat messageFormat = this.getPropertiesHolder(locale)
                .map(propHolder -> propHolder.getMessageFormat(code))
                .orElse(null);
        if (isNull(messageFormat) && nonNull(defaultLocale) && !defaultLocale.equals(locale)) {
            messageFormat = this.getPropertiesHolder(defaultLocale)
                    .map(propHolder -> propHolder.getMessageFormat(code))
                    .orElse(null);
        }
        return messageFormat;
    }

    protected abstract Map<Locale, PropertiesHolder> loadCache();

    protected void mergeProperties(Locale locale, PropertiesHolder holder) {
        final PropertiesHolder currentPropHolder = cachedProperties.get(locale);
        if(isNull(currentPropHolder)){
            cachedProperties.put(locale, holder);
        } else {
            currentPropHolder.properties.putAll(holder.properties);
            currentPropHolder.cachedMessageFormats.putAll(holder.cachedMessageFormats);
        }
    }

    private Optional<PropertiesHolder> getPropertiesHolder(Locale locale) {
        return Optional.ofNullable(this.cachedProperties.get(locale));
    }

    protected class PropertiesHolder {
        private final Map<String, String> properties;
        private final Map<String, MessageFormat> cachedMessageFormats;

        public PropertiesHolder(Map<String, String> properties, Locale locale) {
            this.properties = requireNonNull(properties);
            this.cachedMessageFormats = new ConcurrentHashMap<>();
            properties.forEach((code, value) -> this.addMessageFormat(code, value, locale));
        }

        public String getProperty(String code) {
            return isNull(this.properties) ? null : this.properties.get(code);
        }

        public MessageFormat getMessageFormat(String code) {
            if (isNull(this.properties) || isNull(this.cachedMessageFormats)) {
                return null;
            } else {
                return this.cachedMessageFormats.get(code);

            }
        }

        private void addMessageFormat(String code, String value, Locale locale) {
            MessageFormat result = AbstractPropertiesHolderMessageSource.this.createMessageFormat(value, locale);
            this.cachedMessageFormats.put(code, result);
        }

    }

}