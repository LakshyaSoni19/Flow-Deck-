package com.technomancarai.tms.config.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI security scheme configuration for JWT Bearer Authentication.
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
@SecurityScheme(
        name = SwaggerConstants.SECURITY_SCHEME_NAME,
        type = SecuritySchemeType.HTTP,
        scheme = SwaggerConstants.SECURITY_SCHEME,
        bearerFormat = SwaggerConstants.BEARER_FORMAT
)
public class SwaggerSecurityConfig {
}
