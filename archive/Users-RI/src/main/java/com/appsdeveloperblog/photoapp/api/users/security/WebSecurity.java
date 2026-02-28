package com.appsdeveloperblog.photoapp.api.users.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Environment environment;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Constructor: inject environment, users service, and password encoder used for authentication
    @Autowired
    public WebSecurity(Environment environment, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.environment = environment;
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /*public WebSecurity(Environment environment) {
        this.environment = environment;
    }*/


    // Configure HTTP security: disable CSRF, restrict access by gateway IP, add auth filter, allow H2 console frames
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("-----------------------------WebSecurity/configure/HttpSecurity--------------------------" + java.time.LocalDateTime.now());
        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/users/**").permitAll();
        //http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"));
        http.authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/users/status/check", "/users", "/users/login").permitAll()
                .antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"))
                .and()
                .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();
    }

    // Create and configure AuthenticationFilter with AuthenticationManager and login URL path
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        System.out.println("-----------------------------WebSecurity/getAuthenticationFilter--------------------------"+java.time.LocalDateTime.now());
        System.out.println("-----------------------------WebSecurity/ set authenticationManager in AuthenticationFilter--------------------------"+java.time.LocalDateTime.now());
        //AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager());
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager());
        //authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
        return authenticationFilter;
    }

    /* So in this method we will need to configure authentication manager builder and let's bring framework
     * know which service is going to be used to load user details and which password encoder is going to be used.
     * @param auth
     * @throws Exception
     */

    // Configure AuthenticationManagerBuilder to use UsersService and BCrypt password encoder
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        System.out.println("-----------------------------WebSecurity/configure/AuthenticationManagerBuilder--------------------------"+java.time.LocalDateTime.now());
        System.out.println("-----------------------------This method is responsible for login--------------------------"+java.time.LocalDateTime.now());
        //auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
        System.out.println(usersService);
        System.out.println(bCryptPasswordEncoder);
        auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
    }
}
