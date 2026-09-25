package com.technomancarai.tms.controller;

import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Tag(name = "11. Notifications", description = "Endpoints for testing SMTP email notification dispatch")
public class TestEmailController {

    private final EmailService emailService;

    @Data
    public static class TestEmailRequest {
        private String recipient = "lakshyasoni0422@gmail.com";
        private String subject = "Flow Deck Test Email";
        private String body = "This is a test email from Spring Boot.";
    }

    @PostMapping("/send-email")
    @Operation(summary = "01. Send Test Email over SMTP", description = "Verifies whether JavaMailSender and SMTP credentials are functionally working independently of business logic.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test email sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "SMTP dispatch error")
    })
    public ResponseEntity<ApiResponse<String>> sendTestEmail(@RequestBody(required = false) TestEmailRequest request) {
        if (request == null) {
            request = new TestEmailRequest();
        }
        String to = (request.getRecipient() != null && !request.getRecipient().isBlank())
                ? request.getRecipient()
                : "lakshyasoni0422@gmail.com";

        log.info("Executing test email dispatch to: {}", to);
        try {
            emailService.sendOtpEmail(to, "999888", "SMTP TEST");
            return ResponseEntity.ok(ApiResponse.success("Test email sent successfully to " + to));
        } catch (Exception e) {
            log.error("Failed to send test email to {}: {}", to, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("SMTP Test Failed: " + e.getMessage()));
        }
    }
}
