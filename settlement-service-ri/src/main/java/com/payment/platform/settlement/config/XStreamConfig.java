package com.payment.platform.settlement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.thoughtworks.xstream.XStream;

/**
 * XStream configuration for the Settlement Service.
 * 
 * This configuration allows payment platform events and models to be
 * properly serialized/deserialized by Axon Framework.
 * 
 * Purpose: Prevents ForbiddenClassException for payment events
 * Compatibility: Works with existing com.appsdeveloperblog.** services
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
