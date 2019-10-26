package com.bookstudy.licenseservice.services;

import com.bookstudy.licenseservice.client.OrganizationDiscoveryClient;
import com.bookstudy.licenseservice.client.OrganizationRestTemplateClient;
import com.bookstudy.licenseservice.client.OrganizationRibbonClient;
import com.bookstudy.licenseservice.config.ServiceConfig;
import com.bookstudy.licenseservice.model.License;
import com.bookstudy.licenseservice.model.Organization;
import com.bookstudy.licenseservice.respository.LicenseRepository;
import com.bookstudy.licenseservice.utils.UserContextHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
//@DefaultProperties(
//        commandProperties = {
//                @HystrixProperty(name="excution.isolation.thread.timeoutInMilliseconds",value="10000")
//        }
//)
public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    @Autowired
    OrganizationDiscoveryClient odc;

    @Autowired
    OrganizationRibbonClient organizationRibbonClient;

//    @Autowired
//    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestTemplateClient;

    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "10000")
            }
    )
     public Organization getOrganizationByOAuth2(String  organizationId){
         Organization org = organizationRestTemplateClient.getOrg(organizationId);
         return org;
     }

    public License getLicense(String organizationId,String licenseId,String clientType) {
        License licenseId1 = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
//        String changeContet = config.getExampleProperty();

//        Organization org = retrieveOrgInfo(organizationId,clientType);

//        Organization org = getOrganization(organizationId);

        //测试Oauth2时调用Org服务的情况
        Organization org  = getOrganizationByOAuth2(organizationId);

        return licenseId1.withOrganizationName(org.getName()).withContactName(org.getContactName())
                .withContactEmail(org.getContactEmail())
                .withContactPhone(org.getContactPhone())
                .withComment(config.getExampleProperty());
    }

    @HystrixCommand
    private Organization getOrganization(String organizationId) {
        Organization organization = null;
        organization =  organizationRibbonClient.getOrganization(organizationId);
        return organization;
    }

    private Organization retrieveOrgInfo(String organizationId, String clientType) {
        Organization organization = null;
        switch(clientType){
            case "fegin":
                System.out.println("I am using the feign client");
//                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRibbonClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization  = odc.getOrganization(organizationId);
                break;
            default:
                System.out.println("I am using the default client");

        }
        return organization;
    }

    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(
                            name="execution.isolation.thread.timeoutInMilliseconds",
                            value="2200"
                    ),
                    @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="10"),
                    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="75"),
                    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="7000"),
                    @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds",value="15000"),
                    @HystrixProperty(name="metrics.rollingStats.numBuckets",value="5")
            },
            fallbackMethod = "buildFallbackLicenseList",
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties = {
                    @HystrixProperty(name="coreSize",value="30"),
                    @HystrixProperty(name="maxQueueSize",value="10")
            }
    )
    public List<License> getLicensesByOrg(String organizationId) {
        logger.info("getLicensesByOrg correlation id : {}", UserContextHolder.getUserContext().getCorrelationId());
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallbackLicenseList(String organizationId){
        List<License> list = new ArrayList<>();

        License license = new License().withId("0000").withOrganizationId(organizationId)
                .withProductName("Sorry");
        list.add(license);
        return list;
    }

    private void randomlyRunLong() {
        Random random = new Random();
        int i = random.nextInt((3 - 1) + 1) + 1;
        if (i==3) sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(3100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
