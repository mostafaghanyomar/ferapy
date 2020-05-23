package com.ferapy.util;

import com.ferapy.FerapyApplication;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.Set;

import static org.springframework.util.StringUtils.isEmpty;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationUtil {

    public static final String DEV = "dev";
    public static final String TEST = "test";
    public static final String PROD = "prod";
    private static final Set<String> PROFILES = Set.of(DEV, TEST, PROD);

    private static final String DEFAULT_PROFILE = DEV;
    private static final String PROFILE_ENV_VAR_NAME = "environment";

    public static SpringApplication setupSpringApplication() {
        SpringApplication application = new SpringApplication(FerapyApplication.class);
        application.setEnvironment(setupEnvironment());
        return application;
    }

    public static String validateAndGetEnvironmentProfile() {
        String envProfile = System.getenv(PROFILE_ENV_VAR_NAME);
        if (!isEmpty(envProfile) && !PROFILES.contains(envProfile)) {
            throw new IllegalStateException("The Environment Is Unknown");
        }
        return isEmpty(envProfile) ? DEFAULT_PROFILE : envProfile;
    }

    private static ConfigurableEnvironment setupEnvironment() {
        String envProfile = validateAndGetEnvironmentProfile();
        ConfigurableEnvironment environment = new StandardServletEnvironment();
        environment.setActiveProfiles(envProfile);
        return environment;
    }

}
