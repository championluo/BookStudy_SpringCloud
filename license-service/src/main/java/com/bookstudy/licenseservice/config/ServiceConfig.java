package com.bookstudy.licenseservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope  // spring boot actuator 提供的通过url来刷新配置
public class ServiceConfig {

    @Value("${example.property}")
    private String exampleProperty;

    @Value("${signing.key}")
    private String jwtKey;

    public String getExampleProperty(){
        return exampleProperty;
    }

    public String getJwtKey() {
        return jwtKey;
    }
}
