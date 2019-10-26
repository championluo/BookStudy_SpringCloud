package com.bookstudy.licenseservice.event.handler;

import com.bookstudy.licenseservice.event.model.OrganizationChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@EnableBinding(Sink.class)
public class OrganizationChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

//    @StreamListener(Sink.INPUT)
    public void loggerSink(OrganizationChangeModel model){
        logger.debug("Received an event for organization id :{}",model.getOrganizationId());
        System.out.println("Received an event for organization id :"+model.getOrganizationId());
    }

}
