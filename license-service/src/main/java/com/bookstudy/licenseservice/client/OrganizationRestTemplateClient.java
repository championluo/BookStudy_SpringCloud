package com.bookstudy.licenseservice.client;

import com.bookstudy.licenseservice.model.Organization;
import com.bookstudy.licenseservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrganizationRestTemplateClient {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Autowired
    OAuth2RestTemplate oAuth2RestTemplate;

    public Organization getOrg(String orgId){

        logger.debug("In Licensing Service.getOrganization: {}", UserContextHolder.getUserContext().getCorrelationId());

        ResponseEntity<Organization> entity = oAuth2RestTemplate.exchange("http://zuulservice/api/organizationservice/v1/organizations/{organizationId}",
                HttpMethod.GET, null, Organization.class, orgId);

        return entity.getBody();
    }
}
