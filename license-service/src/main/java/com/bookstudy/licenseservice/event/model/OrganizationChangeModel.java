package com.bookstudy.licenseservice.event.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrganizationChangeModel {

    private String type;
    private String action;
    private String organizationId;
    private String correlationId;

    public OrganizationChangeModel(String typeName, String action, String orgId, String correlationId) {
        super();
        this.type   = type;
        this.action = action;
        this.organizationId = organizationId;
        this.correlationId = correlationId;
    }
}
