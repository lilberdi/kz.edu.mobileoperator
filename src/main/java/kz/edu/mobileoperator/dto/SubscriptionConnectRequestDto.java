package kz.edu.mobileoperator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для подключения тарифа клиенту.
 */
@Getter
@Setter
public class SubscriptionConnectRequestDto {

    @NotNull
    private Long customerId;

    @NotNull
    private Long tariffId;
}


