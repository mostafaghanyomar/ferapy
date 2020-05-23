package com.ferapy.config.i18n.messagesource;

import com.ferapy.config.i18n.constant.Language;
import org.springframework.context.MessageSource;

import java.util.Optional;

public interface Translator extends MessageSource {

    Optional<String> translateTo(String key, Language language);

    Optional<String> translateWithParamsTo(String key, Language language, Object... params);

}
