package kz.edu.mobileoperator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Краткая информация об аутентифицированном пользователе для отображения в UI.
 */
@Getter
@AllArgsConstructor
public class AuthInfoDto {

    private String username;
    private String role;
}


