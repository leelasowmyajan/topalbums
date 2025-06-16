package com.leelasowmya.topalbums.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static com.leelasowmya.topalbums.constant.Constant.X_REQUESTED_WITH;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

// Marks this class as a configuration class in Spring.
@Configuration
public class CorsConfig {
    // Method annotated with @Bean is used to mark a method as a bean producer
    // In this case, it creates and returns a CorsFilter bean
    @Bean
    public CorsFilter corsFilter() {
        // UrlBasedCorsConfigurationSource - source for configuring CORS for specific URL patterns
        var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        // CorsConfiguration - used to define CORS settings
        var corsConfiguration = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers, etc.) to be included in cross-origin requests
        corsConfiguration.setAllowCredentials(true);

        // Define which origins are allowed to make requests to this server
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));  // Allow only React app running on localhost:3000

        // Define which headers are allowed in the request
        corsConfiguration.setAllowedHeaders(List.of(
                ORIGIN,  // The Origin header indicates where the request is coming from
                ACCESS_CONTROL_ALLOW_ORIGIN,  // Header to indicate which origins can access resources
                CONTENT_TYPE,  // Specifies the media type of the resource (e.g., application/json)
                ACCEPT,  // Specifies the types of responses that are acceptable
                AUTHORIZATION,  // Allow the Authorization header (used for authentication)
                X_REQUESTED_WITH,  // A header that is commonly used in AJAX requests to identify the request
                ACCESS_CONTROL_REQUEST_METHOD,  // Indicates which HTTP method is used in the request (GET, POST, etc.)
                ACCESS_CONTROL_REQUEST_HEADERS,  // The headers being requested
                ACCESS_CONTROL_ALLOW_CREDENTIALS  // Allow credentials like cookies in cross-origin requests
        ));

        // Expose specific headers so that they are accessible to the frontend
        corsConfiguration.setExposedHeaders(List.of(
                ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION,
                X_REQUESTED_WITH, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS,
                ACCESS_CONTROL_ALLOW_CREDENTIALS
        ));

        // Define which HTTP methods are allowed in cross-origin requests
        corsConfiguration.setAllowedMethods(List.of(
                GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name()
        ));

        // Register this CORS configuration for all endpoints (/** means all paths)
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        // Return a new instance of CorsFilter with the configuration
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
