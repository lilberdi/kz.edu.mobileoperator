package kz.edu.mobileoperator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Универсальный DTO для запросов смены статуса сущностей (тариф, клиент, подписка).
 */
@Getter
@Setter
public class StatusUpdateRequest {

    @NotBlank
    private String status;
}


