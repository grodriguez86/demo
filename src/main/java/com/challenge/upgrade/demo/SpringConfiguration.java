package com.challenge.upgrade.demo;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;

@Configuration
public class SpringConfiguration {


    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Bean
    public CloudantClient cloudantClient() throws MalformedURLException {
        return ClientBuilder.url(new URL(url))
                .username(user)
                .password(password)
                .build();
    }

}