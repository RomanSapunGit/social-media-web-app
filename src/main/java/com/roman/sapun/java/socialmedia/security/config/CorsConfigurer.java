package com.roman.sapun.java.socialmedia.security.config;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfigurer implements WebMvcConfigurer {
    private final ValueConfig valueConfig;

    @Autowired
    public CorsConfigurer(ValueConfig valueConfig) {
        this.valueConfig = valueConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(valueConfig.getUrl())
                .maxAge(3600L)
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Upgrade")
                .allowCredentials(true);
    }
}
