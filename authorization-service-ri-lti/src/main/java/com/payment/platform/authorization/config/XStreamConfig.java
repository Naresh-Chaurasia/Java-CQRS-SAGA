package com.payment.platform.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thoughtworks.xstream.XStream;

/**
 * XStream configuration for secure XML serialization in the authorization service.
 * Configures allowed package patterns for payment platform events and models to prevent
 * security vulnerabilities during event serialization/deserialization with Axon Framework.
 */
@Configuration
public class XStreamConfig {	

    @Bean    
    XStream xStream() {		
        XStream xStream = new XStream();
        // Allow payment platform events and models
        xStream.allowTypesByWildcard(new String[] { 
            "com.payment.platform.**",
            "com.appsdeveloperblog.**"  // For compatibility with existing services
        });		
        return xStream;	
    }
}
