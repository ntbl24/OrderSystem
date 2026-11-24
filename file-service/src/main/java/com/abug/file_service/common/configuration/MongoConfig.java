package com.abug.file_service.common.configuration;

import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class MongoConfig {

    @Bean
    MongoClientSettingsBuilderCustomizer uuidCustomizer(){
        return clientSettingsBuilder ->
                clientSettingsBuilder.uuidRepresentation(UuidRepresentation.STANDARD);
    }
}
