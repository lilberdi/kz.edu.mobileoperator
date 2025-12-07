package kz.edu.mobileoperator.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO для выдачи ежемесячного платежа по подписке.
 */
@Getter
@AllArgsConstructor
public class MonthlyChargeResponseDto {

    private BigDecimal amount;
}


