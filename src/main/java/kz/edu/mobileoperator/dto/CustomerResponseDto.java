package kz.edu.mobileoperator.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для выдачи информации о клиенте наружу.
 */
@Getter
@Setter
public class CustomerResponseDto {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String documentNumber;
    private String status;

    /**
     * Упрощённый список идентификаторов подписок клиента.
     * Можно расширить при необходимости.
     */
    private List<Long> subscriptionIds;
}


