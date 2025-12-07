package kz.edu.mobileoperator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для передачи статистики по подпискам в разрезе месяцев.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatsDto {

    /**
     * Месяц в формате YYYY-MM (например, 2025-01).
     */
    private String month;

    private int activeCount;
    private int newCount;
    private int terminatedCount;
}


