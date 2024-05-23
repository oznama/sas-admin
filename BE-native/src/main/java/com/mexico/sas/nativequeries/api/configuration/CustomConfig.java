package com.mexico.sas.nativequeries.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/queries/base.properties")
})
public class CustomConfig {
}
