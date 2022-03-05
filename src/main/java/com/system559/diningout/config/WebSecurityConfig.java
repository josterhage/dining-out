package com.system559.diningout.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //TODO: build production configuration

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/**").authenticated()
                .antMatchers(HttpMethod.PUT,"/api/**").authenticated()
                .antMatchers(HttpMethod.PATCH,"/api/**").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/**").authenticated()
                .antMatchers(HttpMethod.GET,"/api/**").permitAll()
                .antMatchers("/**").permitAll();
    }
}
