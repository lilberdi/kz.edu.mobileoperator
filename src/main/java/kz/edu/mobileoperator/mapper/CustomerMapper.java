package kz.edu.mobileoperator.mapper;

import java.util.stream.Collectors;
import kz.edu.mobileoperator.dto.CustomerRequestDto;
import kz.edu.mobileoperator.dto.CustomerResponseDto;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.Subscription;
import org.springframework.stereotype.Component;

/**
 * Маппер между сущностью {@link Customer} и DTO.
 */
@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequestDto dto) {
        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setDocumentNumber(dto.getDocumentNumber());
        return customer;
    }

    public CustomerResponseDto toDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setDocumentNumber(customer.getDocumentNumber());
        dto.setStatus(customer.getStatus().name());

        if (customer.getSubscriptions() != null) {
            dto.setSubscriptionIds(
                    customer.getSubscriptions()
                            .stream()
                            .map(Subscription::getId)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }
}


