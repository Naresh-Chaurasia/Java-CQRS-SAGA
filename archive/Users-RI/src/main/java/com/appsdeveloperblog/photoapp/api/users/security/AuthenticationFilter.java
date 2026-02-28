package com.appsdeveloperblog.photoapp.api.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UsersService usersService;
    private final Environment environment;
    
    public AuthenticationFilter(UsersService usersService, 
            Environment environment, 
            AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager);
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                            HttpServletResponse res) throws AuthenticationException {
        System.out.println("-----------------------------AuthenticationFilter/attemptAuthentication--------------------------"+java.time.LocalDateTime.now());

        try {
            LoginRequestModel creds = new ObjectMapper()
                    .readValue(req.getInputStream(), LoginRequestModel.class);
            
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                        HttpServletResponse res,
                                        FilterChain chain,
                                        Authentication auth) throws IOException, ServletException {
        System.out.println("-----------------------------AuthenticationFilter/successfulAuthentication--------------------------"+java.time.LocalDateTime.now());
        System.out.println("---------------token.expiration_time------------"+environment.getProperty("token.expiration_time"));
        System.out.println("---------------token.secret------------"+environment.getProperty("token.secret"));

        String userName = ((User) auth.getPrincipal()).getUsername();
        UserDto userDetails = usersService.getUserDetailsByEmail(userName);
        
        try {
            // Decode the Base64 secret
            String tokenSecret = environment.getProperty("token.secret");
            byte[] secretBytes = Base64.getDecoder().decode(tokenSecret);
            SecretKeySpec key = new SecretKeySpec(secretBytes, "HmacSHA512");
            
            // Generate token with the decoded key
            String token = Jwts.builder()
                    .setSubject(userDetails.getUserId())
                    .setExpiration(new Date(System.currentTimeMillis() + 
                            Long.parseLong(environment.getProperty("token.expiration_time"))))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
            
            res.addHeader("token", token);
            res.addHeader("userId", userDetails.getUserId());
            
        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            throw new ServletException("Failed to generate authentication token", e);
        }
    }
}