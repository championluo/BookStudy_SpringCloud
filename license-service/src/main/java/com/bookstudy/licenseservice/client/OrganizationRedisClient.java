package com.bookstudy.licenseservice.client;

import com.bookstudy.licenseservice.model.Organization;
import com.bookstudy.licenseservice.respository.OrganizationRedisRepository;
import com.bookstudy.licenseservice.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrganizationRedisClient {

    @Autowired
//    RestTemplate restTemplate;
    OAuth2RestTemplate oAuth2RestTemplate;

    @Autowired
    OrganizationRedisRepository orgRedisRepostory;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRedisClient.class);

    public Organization getCacheOrg(String orgId){
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());
        System.out.println("In Licensing Service.getOrganization: "+UserContext.getCorrelationId());

        Organization organization = checkRedisCache(orgId);
        if (organization != null) {
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", orgId, organization);
            System.out.println("I have successfully retrieved an organization "+orgId+" from the redis cache: "+ organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", orgId);
        System.out.println("Unable to locate organization from the redis cache: ."+orgId);

//        ResponseEntity<Organization> restExchange =
//                oAuth2RestTemplate.exchange(
//                        "http://localhost:5555/api/organizationservice/v1/organizations/{organizationId}",
//                        HttpMethod.GET,
//                        null, Organization.class, orgId);

        ResponseEntity<Organization> restExchange =
                oAuth2RestTemplate.exchange(
                        "http://localhost:8081/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, orgId);

        Organization body = restExchange.getBody();

        if (body != null) {
            cacheOrganizationObject(body);
        }

        return body;
    }

    private void cacheOrganizationObject(Organization body) {
        try {
            orgRedisRepostory.saveOrganization(body);
        } catch (Exception ex) {
            logger.error("Unable to cache organization {} in Redis. Exception {}", body.getId(), ex);
        }
    }

    private Organization checkRedisCache(String orgId) {
        try {
            return orgRedisRepostory.findOrganization(orgId);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", orgId, e);
            return null;
        }
    }


}
