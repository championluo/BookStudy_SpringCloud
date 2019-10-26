package com.bookstudy.licenseservice;

import com.bookstudy.licenseservice.config.ServiceConfig;
import com.bookstudy.licenseservice.event.model.OrganizationChangeModel;
import com.bookstudy.licenseservice.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
//@EnableDiscoveryClient  //当时用discoveryClient时有效
//@EnableFeignClients //当时用OpenFeign时才有效
@EnableEurekaClient
@EnableCircuitBreaker
@EnableResourceServer
@EnableOAuth2Client
@EnableBinding(Sink.class)
//@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class LicenseServiceApplication {

    @Autowired
    private ServiceConfig serviceConfig;
    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceApplication.class);

    @StreamListener(Sink.INPUT)
    public void loggerSink(OrganizationChangeModel model){
        logger.debug("Received an event for organization id :{}",model.getOrganizationId());
        System.out.println("Received an event for organization id :"+model.getOrganizationId());
    }

    @LoadBalanced
    @Bean
    @Primary
    public RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (interceptors == null) {
            restTemplate.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            restTemplate.setInterceptors(interceptors);
        }
        return restTemplate;
    }

    @Bean
    public OAuth2RestTemplate restTemplate(UserInfoRestTemplateFactory factory) {
        return factory.getUserInfoRestTemplate();
    }

//    @Bean
//    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails details,
//                                                 @Qualifier("oauth2ClientContext") OAuth2ClientContext oauth2ClientContext) {
//        return new OAuth2RestTemplate(details, oauth2ClientContext);
//    }

    public static void main(String[] args) {
        SpringApplication.run(LicenseServiceApplication.class, args);
    }

}
