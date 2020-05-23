package com.ferapy.config.i18n.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Translations {

    private static final String APP_I18N = "i18n/APP_I18N";

    public static final Set<String> BUNDLES_BASE_NAMES =
            Set.of(
                    APP_I18N
            );

    public static Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        final ResourceBundle appPropsBundle = ResourceBundle.getBundle(APP_I18N, Language.DEFAULT_LANGUAGE.getLocale());
        keys.addAll(appPropsBundle.keySet());
        //Add More keys...

        return Collections.unmodifiableSet(keys);
    }

}
