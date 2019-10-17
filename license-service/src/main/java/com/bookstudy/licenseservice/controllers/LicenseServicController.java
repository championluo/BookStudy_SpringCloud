package com.bookstudy.licenseservice.controllers;

import com.bookstudy.licenseservice.model.License;
import com.bookstudy.licenseservice.services.LicenseService;
import com.bookstudy.licenseservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/organizations/{organizationId}/licenses")
public class LicenseServicController {

    private static final Logger logger = LoggerFactory.getLogger(LicenseServicController.class);

    @Autowired
    private LicenseService licenseService;

//    @RequestMapping(value = "/{licenseId}",method = RequestMethod.GET)
//    public License getLicens(
//            @PathVariable("organizationId") String organizationId,
//            @PathVariable("licenseId") String licenseId){
//        return new License().withId(licenseId)
//                .withProductName("Teleco")
//                .withOrganizationId("TestOrg")
//                .withLicenseType("Seat");
//    }


//    @RequestMapping(value = "/oauth",method = RequestMethod.GET)
//    public Organization getOrg(@PathVariable("organizationId") String organizationId){
//        return licenseService.getOrganizationByOAuth2(organizationId);
//    }

    @RequestMapping(value="/",method = RequestMethod.GET)
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) {

        logger.info("LicenseServicController.getLicenses Correlation_id: {}", UserContextHolder.getUserContext().getCorrelationId());
        return licenseService.getLicensesByOrg(organizationId);
    }

    @RequestMapping(value="/{licenseId}/{clientType}",method = RequestMethod.GET)
    public License getLicenses( @PathVariable("organizationId") String organizationId,
                                @PathVariable("licenseId") String licenseId,
                                @PathVariable("clientType") String clientType) {

        return licenseService.getLicense(organizationId,licenseId,clientType);
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.PUT)
    public String updateLicenses( @PathVariable("licenseId") String licenseId) {
        return String.format("This is the put");
    }

    @RequestMapping(value="/",method = RequestMethod.POST)
    public void saveLicenses(@RequestBody License license) {
//        licenseService.saveLicense(license);
        return ;
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteLicenses( @PathVariable("licenseId") String licenseId) {
        return String.format("This is the Delete");
    }

}
