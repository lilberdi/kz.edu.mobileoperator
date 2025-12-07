package kz.edu.mobileoperator.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO c агрегированными показателями для дэшборда админ-панели.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private long totalCustomers;
    private long activeTariffs;
    private long activeSubscriptions;
    private BigDecimal monthlyRevenue;
}


