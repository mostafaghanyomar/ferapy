package com.ferapy.config;

import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.ferapy.util.ApplicationUtil.PROD;
import static com.ferapy.util.ApplicationUtil.validateAndGetEnvironmentProfile;
import static java.util.Objects.nonNull;

@Getter
@Configuration
public class AppConfig {

    private static final String TAGS_DELIMITER = ".";
    private static final String ENV_PROFILE = validateAndGetEnvironmentProfile();
    private static AppConfig instance;


    //Maven Config
    @Value("${app.name}")
    private String appName;
    @Value("${app.description}")
    private String appDescription;
    @Value("${app.version}")
    private String appVersion;

    @Value("${spring.data.mongodb.service-name}")
    private String mongodbServiceName;

    public boolean isProd() {
        return nonNull(ENV_PROFILE) && PROD.equals(ENV_PROFILE);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }

    @PostConstruct
    public void buildPostConstructValues() {
        AppConfig.instance = this;
    }

}
