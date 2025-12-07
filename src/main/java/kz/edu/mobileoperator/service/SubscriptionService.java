package kz.edu.mobileoperator.service;

import java.math.BigDecimal;
import java.util.List;
import kz.edu.mobileoperator.model.Subscription;

/**
 * Сервисный слой для работы с подписками клиентов на тарифы.
 */
public interface SubscriptionService {

    /**
     * Подключить клиенту тариф (создать активную подписку).
     */
    Subscription connectTariff(Long customerId, Long tariffId);

    /**
     * Изменить тариф у существующей подписки.
     */
    Subscription changeTariff(Long subscriptionId, Long newTariffId);

    /**
     * Расторгнуть подписку (изменить статус и дату окончания).
     */
    Subscription terminateSubscription(Long subscriptionId);

    /**
     * Рассчитать ежемесячный платёж по подписке.
     * В простой реализации он равен абонентской плате тарифа.
     */
    BigDecimal calculateMonthlyCharge(Long subscriptionId);

    /**
     * Получить список всех подписок.
     */
    List<Subscription> getAll();
}




