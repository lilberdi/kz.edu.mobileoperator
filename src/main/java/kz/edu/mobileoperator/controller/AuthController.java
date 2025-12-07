package kz.edu.mobileoperator.controller;

import kz.edu.mobileoperator.dto.AuthInfoDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Простой REST-контроллер, который возвращает информацию о текущем пользователе.
 * Используется админ-панелью, чтобы показать "Signed in as ...".
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public AuthInfoDto currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthInfoDto("anonymous", "ANONYMOUS");
        }
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_UNKNOWN");
        return new AuthInfoDto(username, role);
    }
}


