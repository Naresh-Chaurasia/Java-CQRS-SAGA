package com.payment.platform.notification.config;

import com.thoughtworks.xstream.XStream;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
// public class XStreamConfig {
    
//     @Bean
//     public XStreamSerializer xStreamSerializer() {
//         XStream xStream = new XStream();
        
//         // Allow all payment platform and notification service types
//         xStream.allowTypes(new String[]{
//             "com.payment.platform.**",
//             "com.payment.platform.notification.**"
//         });
        
//         // Configure security framework
//         XStream.setupDefaultSecurity(xStream);
//         xStream.allowTypesByWildcard(new String[]{
//             "com.payment.platform.**",
//             "com.payment.platform.notification.**"
//         });
        
//         return XStreamSerializer.builder()
//             .xStream(xStream)
//             .build();
//     }
// }

@Configuration
public class XStreamConfig {	

    @Bean    
    XStream xStream() {		
        XStream xStream = new XStream();
        // Allow payment platform events and models
        xStream.allowTypesByWildcard(new String[] { 
            "com.payment.platform.**",
    "com.payment.platform.notification.**"
        });		
        return xStream;	
    }
}