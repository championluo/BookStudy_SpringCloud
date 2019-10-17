package com.bookstudy.organizationservice.controller;

import com.bookstudy.organizationservice.model.Organization;
import com.bookstudy.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @RequestMapping(value = "/{organizationId}",method = RequestMethod.GET)
    public Organization getOrg(@PathVariable("organizationId") String organizationId){

        Organization org = organizationService.getOrg(organizationId);
        return org;
    }

    @RequestMapping(value = "/{organizationId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganizaiton(@PathVariable("organizationId") String organizationId){
        organizationService.deleteOrg(organizationId);
    }

}
