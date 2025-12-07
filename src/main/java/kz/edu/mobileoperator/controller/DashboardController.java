package kz.edu.mobileoperator.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import kz.edu.mobileoperator.dto.DashboardSummaryDto;
import kz.edu.mobileoperator.dto.SubscriptionStatsDto;
import kz.edu.mobileoperator.model.Subscription;
import kz.edu.mobileoperator.model.SubscriptionStatus;
import kz.edu.mobileoperator.model.TariffStatus;
import kz.edu.mobileoperator.repository.CustomerRepository;
import kz.edu.mobileoperator.repository.SubscriptionRepository;
import kz.edu.mobileoperator.repository.TariffRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер, который предоставляет агрегированные данные для дэшборда админ-панели.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final TariffRepository tariffRepository;
    private final SubscriptionRepository subscriptionRepository;

    public DashboardController(CustomerRepository customerRepository,
                               TariffRepository tariffRepository,
                               SubscriptionRepository subscriptionRepository) {
        this.customerRepository = customerRepository;
        this.tariffRepository = tariffRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Сводные показатели по клиентам, тарифам и подпискам.
     */
    @GetMapping("/summary")
    public DashboardSummaryDto getSummary() {
        long totalCustomers = customerRepository.count();
        long activeTariffs = tariffRepository.countByStatus(TariffStatus.ACTIVE);
        long activeSubscriptions = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);

        // Простейшая оценка месячной выручки: суммарная абонентская плата активных подписок.
        List<Subscription> activeSubs = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
        BigDecimal monthlyRevenue = activeSubs.stream()
                .map(sub -> sub.getTariff() != null ? sub.getTariff().getMonthlyFee() : null)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardSummaryDto(
                totalCustomers,
                activeTariffs,
                activeSubscriptions,
                monthlyRevenue
        );
    }

    /**
     * Статистика по подпискам по месяцам для отображения на графике.
     * <p>
     * Для простоты считаем, что "newCount" — количество подписок с датой начала в месяце,
     * "terminatedCount" — количество подписок, у которых дата окончания попадает в этот месяц,
     * "activeCount" — количество активных подписок к концу месяца.
     */
    @GetMapping("/subscriptions-stats")
    public List<SubscriptionStatsDto> getSubscriptionsStats() {
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        if (allSubscriptions.isEmpty()) {
            return List.of();
        }

        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth startMonth = currentMonth.minusMonths(11); // последние 12 месяцев

        Map<YearMonth, List<Subscription>> byStartMonth = allSubscriptions.stream()
                .filter(s -> s.getStartDate() != null)
                .collect(Collectors.groupingBy(s -> YearMonth.from(s.getStartDate())));

        Map<YearMonth, List<Subscription>> byEndMonth = allSubscriptions.stream()
                .filter(s -> s.getEndDate() != null)
                .collect(Collectors.groupingBy(s -> YearMonth.from(s.getEndDate())));

        YearMonth ym = startMonth;
        List<SubscriptionStatsDto> result = new java.util.ArrayList<>();
        while (!ym.isAfter(currentMonth)) {
            // копируем значение в effectively final переменную для использования в лямбдах
            YearMonth month = ym;

            List<Subscription> startedInMonth = byStartMonth.getOrDefault(month, List.of());
            List<Subscription> endedInMonth = byEndMonth.getOrDefault(month, List.of());

            long newCount = startedInMonth.size();
            long terminatedCount = endedInMonth.size();

            LocalDate endOfMonth = month.atEndOfMonth();
            long activeCount = allSubscriptions.stream()
                    .filter(s -> !s.getStartDate().isAfter(endOfMonth))
                    .filter(s -> s.getEndDate() == null || !s.getEndDate().isBefore(month.atDay(1)))
                    .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                    .count();

            result.add(new SubscriptionStatsDto(
                    month.toString(),
                    (int) activeCount,
                    (int) newCount,
                    (int) terminatedCount
            ));
            ym = ym.plusMonths(1);
        }

        return result;
    }
}


