package com.bookstudy.licenseservice.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannel {

    @Input("inboundOrgChanges")
    SubscribableChannel orgs();
}
