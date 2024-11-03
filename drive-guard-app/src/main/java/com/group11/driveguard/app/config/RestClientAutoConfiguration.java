package com.group11.driveguard.app.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@AutoConfiguration(after = org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration.class)
@ConditionalOnClass(RestClient.class)
@ConditionalOnBean(RestClient.Builder.class)
public class RestClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}