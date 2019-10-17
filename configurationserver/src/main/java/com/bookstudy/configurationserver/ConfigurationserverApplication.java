package com.bookstudy.configurationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class ConfigurationserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationserverApplication.class, args);
    }

}
