package kz.edu.mobileoperator.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import kz.edu.mobileoperator.exception.BusinessException;
import kz.edu.mobileoperator.exception.ResourceNotFoundException;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.CustomerStatus;
import kz.edu.mobileoperator.model.Subscription;
import kz.edu.mobileoperator.model.SubscriptionStatus;
import kz.edu.mobileoperator.model.Tariff;
import kz.edu.mobileoperator.model.TariffStatus;
import kz.edu.mobileoperator.repository.CustomerRepository;
import kz.edu.mobileoperator.repository.SubscriptionRepository;
import kz.edu.mobileoperator.repository.TariffRepository;
import kz.edu.mobileoperator.service.RecentActivityService;
import kz.edu.mobileoperator.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с подписками клиентов на тарифы.
 */
@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final CustomerRepository customerRepository;
    private final TariffRepository tariffRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RecentActivityService recentActivityService;

    public SubscriptionServiceImpl(CustomerRepository customerRepository,
                                   TariffRepository tariffRepository,
                                   SubscriptionRepository subscriptionRepository,
                                   RecentActivityService recentActivityService) {
        this.customerRepository = customerRepository;
        this.tariffRepository = tariffRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.recentActivityService = recentActivityService;
    }

    @Override
    public Subscription connectTariff(Long customerId, Long tariffId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id=" + customerId));
        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id=" + tariffId));

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("Cannot connect tariff for blocked customer");
        }
        if (tariff.getStatus() == TariffStatus.ARCHIVED) {
            throw new BusinessException("Cannot connect archived tariff");
        }

        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setTariff(tariff);
        subscription.setStartDate(LocalDate.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setCurrentBalance(BigDecimal.ZERO);

        Subscription saved = subscriptionRepository.save(subscription);
        recentActivityService.record(
                "SUBSCRIPTION_CREATED",
                "Subscription created for customer " + customerId + " on tariff " + tariff.getName(),
                "SUBSCRIPTION",
                saved.getId()
        );
        return saved;
    }

    @Override
    public Subscription changeTariff(Long subscriptionId, Long newTariffId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id=" + subscriptionId));
        Tariff newTariff = tariffRepository.findById(newTariffId)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id=" + newTariffId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessException("Only active subscriptions can change tariff");
        }
        if (newTariff.getStatus() == TariffStatus.ARCHIVED) {
            throw new BusinessException("Cannot switch to archived tariff");
        }

        subscription.setTariff(newTariff);
        Subscription saved = subscriptionRepository.save(subscription);
        recentActivityService.record(
                "SUBSCRIPTION_TARIFF_CHANGED",
                "Subscription " + saved.getId() + " switched to tariff " + newTariff.getName(),
                "SUBSCRIPTION",
                saved.getId()
        );
        return saved;
    }

    @Override
    public Subscription terminateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id=" + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.TERMINATED) {
            return subscription; // уже расторгнута
        }

        subscription.setStatus(SubscriptionStatus.TERMINATED);
        subscription.setEndDate(LocalDate.now());
        Subscription saved = subscriptionRepository.save(subscription);
        recentActivityService.record(
                "SUBSCRIPTION_TERMINATED",
                "Subscription terminated: " + saved.getId(),
                "SUBSCRIPTION",
                saved.getId()
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateMonthlyCharge(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id=" + subscriptionId));
        return subscription.getTariff().getMonthlyFee();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription updateStatus(Long id, SubscriptionStatus status) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id=" + id));
        subscription.setStatus(status);
        if (status == SubscriptionStatus.TERMINATED && subscription.getEndDate() == null) {
            subscription.setEndDate(LocalDate.now());
        }
        Subscription saved = subscriptionRepository.save(subscription);
        recentActivityService.record(
            "SUBSCRIPTION_STATUS_CHANGED",
            "Subscription status set to " + status + ": " + saved.getId(),
            "SUBSCRIPTION",
            saved.getId()
        );
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id=" + id));
        subscriptionRepository.delete(subscription);
        recentActivityService.record(
                "SUBSCRIPTION_DELETED",
                "Subscription deleted: " + subscription.getId(),
                "SUBSCRIPTION",
                subscription.getId()
        );
    }
}


