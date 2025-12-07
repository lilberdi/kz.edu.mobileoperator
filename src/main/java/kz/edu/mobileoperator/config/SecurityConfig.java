package kz.edu.mobileoperator.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация Spring Security.
 * <p>
 * Для учебного проекта используем form-login и простое разграничение прав:
 * ADMIN имеет полный доступ, USER имеет только права чтения к части API.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Для простоты в учебном проекте отключаем CSRF.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // статика (JS, CSS, картинки и т.п.)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // страницы логина и вспомогательные HTML/JS
                        .requestMatchers("/login", "/login.html", "/error").permitAll()
                        .requestMatchers("/admin.html", "/admin.js").permitAll()
                        // доступ к админ-панели (корневой маршрут и /admin) только для ADMIN
                        .requestMatchers("/", "/admin").hasRole("ADMIN")
                        // чтение тарифов и клиентов доступно и USER, и ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/tariffs/**", "/api/customers/**")
                        .hasAnyRole("ADMIN", "USER")
                        // вся остальная часть API только для ADMIN
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        // все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Кодировщик паролей, используется при сохранении и проверке пароля.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}


