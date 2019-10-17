package com.bookstudy.authenticationservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("userDetailsServiceBean")
    @Autowired
    private UserDetailsService userDetailsService;

        //password加密的方式 相当于把PasswordEncoder类对象 注册到容器中
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 加密方式
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder;

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {


//        clients.inMemory()
//                .withClient("eagleeye")
//                .secret(passwordEncoder().encode("thisiseagleeye"))
//                .authorizedGrantTypes("password", "refresh_token", "client_credentials")
//                .scopes("webclient", "mobileclient");

        clients.inMemory()
                .withClient("eagleeye")
                .secret(passwordEncoder().encode("thisiseagleeye"))
                .authorizedGrantTypes("password", "refresh_token", "client_credentials")
                .scopes("webclient", "mobileclient");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
      endpoints
        .authenticationManager(authenticationManager)
        .userDetailsService(userDetailsService);
    }

//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//         security.allowFormAuthenticationForClients();
//         security.checkTokenAccess("permitAll()");
//    }
}
