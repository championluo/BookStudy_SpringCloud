package com.bookstudy.specialroutesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class SpecialroutesServiceApplication {

//    @Bean
//    public Filter userContextFilter() {
//        UserContextFilter userContextFilter = new UserContextFilter();
//        return userContextFilter;
//    }

    public static void main(String[] args) {
        SpringApplication.run(SpecialroutesServiceApplication.class, args);
    }

}
