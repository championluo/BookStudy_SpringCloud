package com.bookstudy.organizationservice.service;

import com.bookstudy.organizationservice.model.Organization;
import com.bookstudy.organizationservice.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

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
}
