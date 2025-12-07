package kz.edu.mobileoperator.config;

import kz.edu.mobileoperator.model.UserAccount;
import kz.edu.mobileoperator.model.UserRole;
import kz.edu.mobileoperator.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Инициализирует в базе двух пользователей (admin и user) при первом запуске.
 */
@Configuration
public class SecurityDataInitializer {

    @Bean
    public CommandLineRunner initDefaultUsers(UserAccountRepository userAccountRepository,
                                             PasswordEncoder passwordEncoder) {
        return args -> {
            // Каждый запуск приложения синхронизируем demo‑пользователей
            // с заранее известными логином/паролем: admin/password и user/password.
            userAccountRepository.findByUsername("admin")
                    .map(existing -> {
                        existing.setPassword(passwordEncoder.encode("password"));
                        existing.setRole(UserRole.ADMIN);
                        existing.setEnabled(true);
                        return existing;
                    })
                    .orElseGet(() -> new UserAccount(
                            "admin",
                            passwordEncoder.encode("password"),
                            UserRole.ADMIN,
                            true
                    ));

            userAccountRepository.findByUsername("user")
                    .map(existing -> {
                        existing.setPassword(passwordEncoder.encode("password"));
                        existing.setRole(UserRole.USER);
                        existing.setEnabled(true);
                        return existing;
                    })
                    .orElseGet(() -> new UserAccount(
                            "user",
                            passwordEncoder.encode("password"),
                            UserRole.USER,
                            true
                    ));
        };
    }
}


