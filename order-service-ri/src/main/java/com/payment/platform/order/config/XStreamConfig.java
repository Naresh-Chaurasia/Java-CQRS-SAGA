package com.payment.platform.order.config;

import com.thoughtworks.xstream.XStream;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XStreamConfig {
    
    @Bean
    public XStreamSerializer xStreamSerializer() {
        XStream xStream = new XStream();
        
        // Allow all payment platform and order service types
        xStream.allowTypes(new String[]{
            "com.payment.platform.**",
            "com.payment.platform.order.**"
        });
        
        // Configure security framework
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[]{
            "com.payment.platform.**",
            "com.payment.platform.order.**"
        });
        
        return XStreamSerializer.builder()
            .xStream(xStream)
            .build();
    }
}