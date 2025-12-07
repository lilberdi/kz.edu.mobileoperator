package kz.edu.mobileoperator.controller;

import jakarta.validation.Valid;
import java.util.List;
import kz.edu.mobileoperator.dto.MonthlyChargeResponseDto;
import kz.edu.mobileoperator.dto.SubscriptionChangeTariffRequestDto;
import kz.edu.mobileoperator.dto.SubscriptionConnectRequestDto;
import kz.edu.mobileoperator.dto.SubscriptionResponseDto;
import kz.edu.mobileoperator.mapper.SubscriptionMapper;
import kz.edu.mobileoperator.model.Subscription;
import kz.edu.mobileoperator.service.SubscriptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для операций с подписками клиентов.
 */
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionController(SubscriptionService subscriptionService,
                                  SubscriptionMapper subscriptionMapper) {
        this.subscriptionService = subscriptionService;
        this.subscriptionMapper = subscriptionMapper;
    }

    /**
     * Получить список всех подписок.
     */
    @GetMapping
    public List<SubscriptionResponseDto> getSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAll();
        return subscriptions.stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    /**
     * Подключить тариф клиенту.
     */
    @PostMapping("/connect")
    public SubscriptionResponseDto connectTariff(@Valid @RequestBody SubscriptionConnectRequestDto requestDto) {
        Subscription subscription = subscriptionService.connectTariff(
                requestDto.getCustomerId(),
                requestDto.getTariffId()
        );
        return subscriptionMapper.toDto(subscription);
    }

    /**
     * Сменить тариф у существующей подписки.
     */
    @PostMapping("/{id}/change-tariff")
    public SubscriptionResponseDto changeTariff(@PathVariable Long id,
                                                @Valid @RequestBody SubscriptionChangeTariffRequestDto requestDto) {
        Subscription subscription = subscriptionService.changeTariff(id, requestDto.getNewTariffId());
        return subscriptionMapper.toDto(subscription);
    }

    /**
     * Расторгнуть подписку.
     */
    @PostMapping("/{id}/terminate")
    public SubscriptionResponseDto terminateSubscription(@PathVariable Long id) {
        Subscription subscription = subscriptionService.terminateSubscription(id);
        return subscriptionMapper.toDto(subscription);
    }

    /**
     * Получить ежемесячный платёж по подписке.
     */
    @GetMapping("/{id}/monthly-charge")
    public MonthlyChargeResponseDto getMonthlyCharge(@PathVariable Long id) {
        return new MonthlyChargeResponseDto(subscriptionService.calculateMonthlyCharge(id));
    }
}




