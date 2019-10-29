package com.bookstudy.licenseservice.respository;

import com.bookstudy.licenseservice.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class OrganizationRedisRepositoryImpl implements OrganizationRedisRepository {

    private static final String HASH_NAME ="organization";
    private HashOperations hashOperations;

    @Qualifier("redisTemplate")
    @Autowired
    RedisTemplate restTemplate;

    @PostConstruct
    private void init(){
        restTemplate.setKeySerializer(new StringRedisSerializer());
        restTemplate.setHashKeySerializer(new StringRedisSerializer());
        hashOperations = restTemplate.opsForHash();
    }

    @Override
    public void saveOrganization(Organization org) {
        hashOperations.put(HASH_NAME,org.getId(),org);
    }

    @Override
    public void updateOrganization(Organization org) {
        hashOperations.put(HASH_NAME,org.getId(),org);
    }

    @Override
    public void deleteOrganization(String organizationId) {
        hashOperations.delete(HASH_NAME,organizationId);
    }

    @Override
    public Organization findOrganization(String organizationId) {
        return (Organization) hashOperations.get(HASH_NAME,organizationId);
    }
}
