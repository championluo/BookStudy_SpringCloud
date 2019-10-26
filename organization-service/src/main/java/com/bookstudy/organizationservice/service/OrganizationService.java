package com.bookstudy.organizationservice.service;

import com.bookstudy.organizationservice.event.source.SimpleSourceBean;
import com.bookstudy.organizationservice.model.Organization;
import com.bookstudy.organizationservice.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SimpleSourceBean simpleSourceBean;

    public Organization getOrg(String organizationId) {
        Optional<Organization> byId = organizationRepository.findById(organizationId);
        if (!byId.isPresent()) {
            return new Organization();
        } else {
           return byId.get();
        }
    }

    public void deleteOrg(String id) {
        organizationRepository.deleteById(id);
    }

    public void saveOrg(Organization org) {
        org.setId(UUID.randomUUID().toString());
        organizationRepository.save(org);
        //发送消息
        simpleSourceBean.publishOrgChange("SAVE",org.getId());
    }

    public void updateOrg(Organization org) {
        organizationRepository.save(org);
        simpleSourceBean.publishOrgChange("UPDATE", org.getId());
    }
}
