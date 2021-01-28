package com.songscorer.songscorer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception{
        /*
         * Disabled for now because csrf mainly occurs when sessions and cookies are used
         * to authenticate a session information. We are using REST api's which are stateless
         * and additionally we are using JWT's for authentication therefore we can disable this feature
         */

        /*
         * Additionally we are only permitting API requests which conform to
         * the url path of /api/auth/** and if any request does not follow this
         * pattern then permissions and requests will be disallowed to the requester
         */
        httpSecurity.csrf().disable()
        .authorizeRequests()
        .antMatchers("/api/auth/**")
        .permitAll()
        .anyRequest()
        .authenticated();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
