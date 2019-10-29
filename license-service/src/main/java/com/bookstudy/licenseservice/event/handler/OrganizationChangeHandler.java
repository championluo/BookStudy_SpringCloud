package com.bookstudy.licenseservice.event.handler;

import com.bookstudy.licenseservice.event.CustomChannel;
import com.bookstudy.licenseservice.event.model.OrganizationChangeModel;
import com.bookstudy.licenseservice.respository.OrganizationRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

//@EnableBinding(Sink.class)
@EnableBinding(CustomChannel.class)
public class OrganizationChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    @Autowired
    private OrganizationRedisRepository organizationRedisRepository;

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel orgChange) {
//        logger.debug("Received an event for organization id :{}",model.getOrganizationId());
//        System.out.println("Received an event for organization id :"+model.getOrganizationId());

        logger.debug("Received a message of type " + orgChange.getType());
        System.out.println("Received an event for organization id :" + orgChange.getOrganizationId());
        switch (orgChange.getAction()) {
            case "GET":
                logger.debug("Received a GET event from the organization service for organization id {}", orgChange.getOrganizationId());
                System.out.println("Received a GET event for organization id :" + orgChange.getOrganizationId());
                break;
            case "SAVE":
                logger.debug("Received a SAVE event from the organization service for organization id {}", orgChange.getOrganizationId());
                System.out.println("Received a SAVE event for organization id :" + orgChange.getOrganizationId());
                break;
            case "UPDATE":
                logger.debug("Received a UPDATE event from the organization service for organization id {}", orgChange.getOrganizationId());
                System.out.println("Received a UPDATE event for organization id :" + orgChange.getOrganizationId());
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            case "DELETE":
                logger.debug("Received a DELETE event from the organization service for organization id {}", orgChange.getOrganizationId());
                System.out.println("Received a DELETE event for organization id :" + orgChange.getOrganizationId());
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            default:
                logger.error("Received an UNKNOWN event from the organization service of type {}", orgChange.getType());
                System.out.println("Received a UNKNOWN event for organization id :" + orgChange.getType());
                break;
        }
    }

}
