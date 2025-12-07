package kz.edu.mobileoperator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для отображения информации о подписке.
 */
@Getter
@Setter
public class SubscriptionResponseDto {

    private Long id;
    private Long customerId;
    private Long tariffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal currentBalance;
}


