package kz.edu.mobileoperator.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для выдачи информации о тарифе.
 */
@Getter
@Setter
public class TariffResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal monthlyFee;
    private Integer includedMinutes;
    private Integer includedSms;
    private Integer includedGb;
    private String status;
}


