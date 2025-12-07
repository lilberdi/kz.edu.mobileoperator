package kz.edu.mobileoperator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для смены тарифа у подписки.
 */
@Getter
@Setter
public class SubscriptionChangeTariffRequestDto {

    @NotNull
    private Long newTariffId;
}


