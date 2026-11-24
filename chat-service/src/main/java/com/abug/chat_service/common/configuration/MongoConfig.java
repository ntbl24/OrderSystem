package com.abug.chat_service.common.configuration;

import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    MongoClientSettingsBuilderCustomizer uuidCustomizer(){
        return clientSettingsBuilder ->
                clientSettingsBuilder.uuidRepresentation(UuidRepresentation.STANDARD);
    }
}
