package com.technomancarai.tms.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaAuditingConfigTest {

    @Autowired
    private AuditorAware<String> auditorProvider;

    @Test
    @DisplayName("Should return SYSTEM when no security context authentication exists")
    void testAuditorAwareFallbackToSystem() {
        SecurityContextHolder.clearContext();
        Optional<String> currentAuditor = auditorProvider.getCurrentAuditor();

        assertThat(currentAuditor)
                .isPresent()
                .contains("SYSTEM");
    }

    @Test
    @DisplayName("Should return authenticated username when security context exists")
    void testAuditorAwareWithAuthenticatedUser() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("test-user", "password", java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> currentAuditor = auditorProvider.getCurrentAuditor();

        assertThat(currentAuditor)
                .isPresent()
                .contains("test-user");

        SecurityContextHolder.clearContext();
    }

}
