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
            if (!userAccountRepository.existsByUsername("admin")) {
                UserAccount admin = new UserAccount(
                        "admin",
                        passwordEncoder.encode("password"),
                        UserRole.ADMIN,
                        true
                );
                userAccountRepository.save(admin);
            }
            if (!userAccountRepository.existsByUsername("user")) {
                UserAccount user = new UserAccount(
                        "user",
                        passwordEncoder.encode("password"),
                        UserRole.USER,
                        true
                );
                userAccountRepository.save(user);
            }
        };
    }
}


