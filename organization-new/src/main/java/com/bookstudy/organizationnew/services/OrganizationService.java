package com.bookstudy.organizationnew.services;

import com.bookstudy.organizationnew.model.Organization;
import com.bookstudy.organizationnew.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    public Organization getOrg(String organizationId) {

        Optional<Organization> byId = orgRepository.findById(organizationId);

        if (byId!=null && byId.isPresent()){
            return byId.get();
        }
        return null;
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);

    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
    }

    public void deleteOrg(Organization org){
        orgRepository.deleteById( org.getId());
    }
}
