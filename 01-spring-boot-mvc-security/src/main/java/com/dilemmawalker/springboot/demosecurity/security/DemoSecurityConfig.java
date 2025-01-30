package com.dilemmawalker.springboot.demosecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurityConfig {

    @Bean
    InMemoryUserDetailsManager inMemoryUserDetailsManager(){

        UserDetails john = User.builder()
                .username("john")
                .password("1234")
                .roles("EMPLOYEE")
                .build();

        UserDetails mary = User.builder()
                .username("mary")
                .password("1234")
                .roles("EMPLOYEE", "MANAGER")
                .build();

        UserDetails susan = User.builder()
                .username("f")
                .password("1234")
                .roles("EMPLOYEE", "MANAGER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(john, mary, susan);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //ensures all requests are authenticated & only then passed
        http.authorizeHttpRequests(configurer ->
                configurer.anyRequest().authenticated())
//                        .anyRequest().permitAll())
                //form login helps to create a custom login form which is helpful in login process
                .formLogin(form ->
                        form
                                .loginPage("/showMyLoginPage")
                                //url which is used to autheticate the user(no controller needed, Spring Rocks :)
                                .loginProcessingUrl("/authenticateTheUser")
                                //permits users to see uptill this point without authentication
                                .permitAll()
                                .defaultSuccessUrl("/", true)
                ).logout(logout -> logout.permitAll());

        return http.build();
    }
}
