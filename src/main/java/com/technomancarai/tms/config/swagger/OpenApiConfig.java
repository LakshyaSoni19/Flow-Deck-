package com.technomancarai.tms.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Main OpenAPI / Swagger bean configuration.
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(SwaggerConstants.API_TITLE)
                        .description(SwaggerConstants.API_DESCRIPTION)
                        .version(SwaggerConstants.API_VERSION)
                        .contact(new Contact()
                                .name(SwaggerConstants.CONTACT_NAME)
                                .email(SwaggerConstants.CONTACT_EMAIL))
                        .license(new License()
                                .name(SwaggerConstants.LICENSE_NAME)
                                .url(SwaggerConstants.LICENSE_URL)))
                .addSecurityItem(new SecurityRequirement().addList(SwaggerConstants.SECURITY_SCHEME_NAME))
                .tags(List.of(
                        new Tag().name(SwaggerConstants.TAG_01_AUTH).description(SwaggerConstants.TAG_01_AUTH_DESC),
                        new Tag().name(SwaggerConstants.TAG_02_ADMIN_APPROVAL).description(SwaggerConstants.TAG_02_ADMIN_APPROVAL_DESC),
                        new Tag().name(SwaggerConstants.TAG_03_USER_MGMT).description(SwaggerConstants.TAG_03_USER_MGMT_DESC),
                        new Tag().name(SwaggerConstants.TAG_04_ROLE_MGMT).description(SwaggerConstants.TAG_04_ROLE_MGMT_DESC),
                        new Tag().name(SwaggerConstants.TAG_05_DEPT_MGMT).description(SwaggerConstants.TAG_05_DEPT_MGMT_DESC),
                        new Tag().name(SwaggerConstants.TAG_06_DESIG_MGMT).description(SwaggerConstants.TAG_06_DESIG_MGMT_DESC),
                        new Tag().name(SwaggerConstants.TAG_07_PROJECT_MGMT).description(SwaggerConstants.TAG_07_PROJECT_MGMT_DESC),
                        new Tag().name(SwaggerConstants.TAG_08_PM_WORKSPACE).description(SwaggerConstants.TAG_08_PM_WORKSPACE_DESC),
                        new Tag().name(SwaggerConstants.TAG_09_EMPLOYEE_WORKSPACE).description(SwaggerConstants.TAG_09_EMPLOYEE_WORKSPACE_DESC),
                        new Tag().name(SwaggerConstants.TAG_10_DASHBOARD).description(SwaggerConstants.TAG_10_DASHBOARD_DESC),
                        new Tag().name(SwaggerConstants.TAG_11_NOTIFICATIONS).description(SwaggerConstants.TAG_11_NOTIFICATIONS_DESC),
                        new Tag().name(SwaggerConstants.TAG_12_REPORTS).description(SwaggerConstants.TAG_12_REPORTS_DESC)
                ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group(SwaggerConstants.PUBLIC_API_GROUP)
                .packagesToScan(SwaggerConstants.CONTROLLER_PACKAGE)
                .build();
    }
}
