package com.bookstudy.authenticationservice.security;

import com.bookstudy.authenticationservice.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 用于管理JWT令牌的创建,签名和翻译
 */
@Configuration
public class JWTTokenStoreConfig {

    @Autowired
    ServiceConfig serviceConfig;

    /**
     * 把配置中心的密钥放到Token生成器中
     * 在JWT和OAuth2服务器之间充当翻译
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //定义将用于签署令牌的签名密钥
        jwtAccessTokenConverter.setSigningKey(serviceConfig.getJwtKey());
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer(){
        return new JWTTokenEnhancer();
    }

    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices defaultTokenService(){
        //用于从出示给服务的令牌中读取数据,即所有需要获取令牌的服务,都用给定的token生成器生成token
        DefaultTokenServices services = new DefaultTokenServices();
        services.setTokenStore(tokenStore());
        services.setSupportRefreshToken(true);
        return services;
    }

}
