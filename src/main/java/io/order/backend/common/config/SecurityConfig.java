package io.order.backend.common.config;

import io.order.backend.common.response.ApiResponse;
import java.util.ArrayList;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JsonMapper json) throws Exception {
        AuthenticationEntryPoint unauthorized = (request, response, exception) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            json.writeValue(response.getOutputStream(), ApiResponse.error("Authentication required"));
        };
        AccessDeniedHandler forbidden = (request, response, exception) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            json.writeValue(response.getOutputStream(), ApiResponse.error("Access denied"));
        };
        var scopes = new JwtGrantedAuthoritiesConverter();
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = new ArrayList<>(scopes.convert(jwt));
            Object roles = jwt.getClaims().get("roles");
            if (roles instanceof List<?> values) {
                values.stream().filter(String.class::isInstance).map(String.class::cast)
                    .filter(role -> role.equals("ADMIN") || role.equals("BUYER"))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).forEach(authorities::add);
            }
            return authorities;
        });
        return http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/v1/payments/vnpay/ipn", "/api/v1/payments/vnpay/return",
                    "/api/v1/payments/callback/vnpay").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()
                .requestMatchers("/api/v1/products", "/api/v1/products/**", "/api/v1/shipments", "/api/v1/shipments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/payments/*/refund", "/api/v1/payments/*/refunds").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized).accessDeniedHandler(forbidden))
            .oauth2ResourceServer(resource -> resource
                .authenticationEntryPoint(unauthorized)
                .accessDeniedHandler(forbidden)
                .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
            .build();
    }
}
