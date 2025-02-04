package com.dilemmawalker.springboot.demosecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class DemoSecurityConfig {

//    @Bean
//    InMemoryUserDetailsManager inMemoryUserDetailsManager(){
//
//        UserDetails john = User.builder()
//                .username("john")
//                .password("{noop}1234")
//                .roles("EMPLOYEE")
//                .build();
//
//        UserDetails mary = User.builder()
//                .username("mary")
//                .password("{noop}1234")
//                .roles("EMPLOYEE", "MANAGER")
//                .build();
//
//        UserDetails susan = User.builder()
//                .username("f")
//                .password("{noop}1234")
//                .roles("EMPLOYEE", "MANAGER", "ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(john, mary, susan);
//    }



//    we are able to directly inject JdbcUserDetailsManager  & access tables with exact same names provided
//    by Spring Security by default.
//    Table name: users & authorities are accessible directly.
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource  ){
        //Rather need to set JdbcUserDetailsManager as a local variable.
//        return new JdbcUserDetailsManager(dataSource);

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        //define query to retrieve user by username
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?"
        );

        //define query to retrieve authorities/roles by username
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, role from roles where user_id=?"

                //we can probably use jpaRepository for a better usecase of not writing sql & rather using
//                built in queries
        );
        //

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //ensures all requests are authenticated & only then passed
        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers("/").hasRole("EMPLOYEE")
                        .requestMatchers("/leaders/**").hasRole("MANAGER")
                        .requestMatchers("/systems/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
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

                        //access custom access Denied page
                ).logout(logout -> logout.permitAll())
                .exceptionHandling(configurer ->
                        configurer
                                .accessDeniedPage("/accessDenied"));

        return http.build();
    }
}
