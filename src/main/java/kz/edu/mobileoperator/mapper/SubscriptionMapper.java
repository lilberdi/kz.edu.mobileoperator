package kz.edu.mobileoperator.mapper;

import kz.edu.mobileoperator.dto.SubscriptionResponseDto;
import kz.edu.mobileoperator.model.Subscription;
import org.springframework.stereotype.Component;

/**
 * Маппер между сущностью {@link Subscription} и DTO.
 */
@Component
public class SubscriptionMapper {

    public SubscriptionResponseDto toDto(Subscription subscription) {
        SubscriptionResponseDto dto = new SubscriptionResponseDto();
        dto.setId(subscription.getId());
        dto.setCustomerId(subscription.getCustomer().getId());
        dto.setTariffId(subscription.getTariff().getId());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setStatus(subscription.getStatus().name());
        dto.setCurrentBalance(subscription.getCurrentBalance());
        return dto;
    }
}


