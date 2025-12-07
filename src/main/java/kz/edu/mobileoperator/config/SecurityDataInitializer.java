package kz.edu.mobileoperator.config;

import kz.edu.mobileoperator.model.UserAccount;
import kz.edu.mobileoperator.model.UserRole;
import kz.edu.mobileoperator.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Инициализирует/обновляет в базе двух demo‑пользователей:
 * admin/password (роль ADMIN) и user/password (роль USER).
 */
@Configuration
public class SecurityDataInitializer {

    @Bean
    public CommandLineRunner initDefaultUsers(UserAccountRepository userAccountRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            // Каждый запуск приложения синхронизируем demo‑пользователей
            // с заранее известными логином/паролем: admin/password и user/password.
            UserAccount admin = userAccountRepository.findByUsername("admin")
                    .orElseGet(() -> new UserAccount(
                            "admin",
                            passwordEncoder.encode("password"),
                            UserRole.ADMIN,
                            true
                    ));
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(UserRole.ADMIN);
            admin.setEnabled(true);
            userAccountRepository.save(admin);

            UserAccount user = userAccountRepository.findByUsername("user")
                    .orElseGet(() -> new UserAccount(
                            "user",
                            passwordEncoder.encode("password"),
                            UserRole.USER,
                            true
                    ));
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            userAccountRepository.save(user);
        };
    }
}


