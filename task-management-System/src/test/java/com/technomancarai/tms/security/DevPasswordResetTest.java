package com.technomancarai.tms.security;

import com.technomancarai.tms.TaskManagementSystemApplication;
import com.technomancarai.tms.dto.request.LoginRequest;
import com.technomancarai.tms.dto.response.LoginResponse;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
@TestPropertySource(properties = {
        "app.dev.reset-user-passwords=true"
})
public class DevPasswordResetTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @org.junit.jupiter.api.BeforeEach
    void setUpMailMock() {
        jakarta.mail.internet.MimeMessage mimeMessage = org.mockito.Mockito.mock(jakarta.mail.internet.MimeMessage.class);
        org.mockito.Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Verify every existing user password is BCrypt hashed for '1234' and non-password fields are untouched")
    void testAllExistingUsersHavePassword1234Hashed() {
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty(), "User list should not be empty");

        for (User user : users) {
            // 1. Password hash exists
            assertNotNull(user.getPassword(), "User password hash must not be null for " + user.getEmail());

            // 2. Password is NOT literal plain text "1234"
            assertNotEquals("1234", user.getPassword(), "User password must not be stored as plain text for " + user.getEmail());

            // 3. Password matches BCrypt hash of "1234"
            assertTrue(passwordEncoder.matches("1234", user.getPassword()),
                    "Password hash must correspond to '1234' for user " + user.getEmail());

            // 4. Non-password attributes are preserved
            assertNotNull(user.getEmail(), "Email must remain intact");
            assertNotNull(user.getFirstName(), "First name must remain intact");
            assertNotNull(user.getLastName(), "Last name must remain intact");

            // 5. Roles remain unchanged
            List<UserRole> roles = userRoleRepository.findByUserId(user.getId());
            assertFalse(roles.isEmpty(), "User role assignments must remain intact for user " + user.getEmail());
        }
    }

    @Test
    @DisplayName("Verify existing user login succeeds with email and password '1234'")
    void testUserLoginSucceedsWithUpdatedPassword() {
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty(), "Users table should have records");

        User testUser = users.stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active users found"));
        LoginRequest request = new LoginRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword("1234");

        LoginResponse response = authService.login(request);
        assertNotNull(response, "Login response should not be null");
        assertNotNull(response.getToken(), "JWT token should be generated successfully on login");
        assertEquals(testUser.getEmail(), response.getUser().getEmail(), "Logged in user email should match");
    }
}
