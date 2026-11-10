package com.abug.payment.common.configuration;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class HttpClientConfig {
    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(15)).callTimeout(Duration.ofSeconds(20))
            .retryOnConnectionFailure(false).followRedirects(false).build();
    }
}
