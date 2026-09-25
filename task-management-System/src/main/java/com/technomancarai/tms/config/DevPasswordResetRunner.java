package com.technomancarai.tms.config;

import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DEVELOPMENT/TESTING ONLY: Startup runner to update all existing user passwords to '1234'.
 * Passwords are secure BCrypt hashes encoded via the project's PasswordEncoder bean.
 * Execution is strictly guarded by app.dev.reset-user-passwords property and non-production profile.
 */
@Slf4j
@Component
@Profile("!prod")
@ConditionalOnProperty(name = "app.dev.reset-user-passwords", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class DevPasswordResetRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[DEV ONLY] Running automatic password sync for existing users...");
        List<User> users = userRepository.findAll();
        int updatedCount = 0;

        for (User user : users) {
            if (user.getPassword() == null || !passwordEncoder.matches("1234", user.getPassword())) {
                user.setPassword(passwordEncoder.encode("1234"));
                userRepository.save(user);
                updatedCount++;
            }
        }

        log.info("[DEV ONLY] Password sync complete. Updated {} out of {} user passwords to BCrypt encoded '1234'.",
                updatedCount, users.size());
    }
}
