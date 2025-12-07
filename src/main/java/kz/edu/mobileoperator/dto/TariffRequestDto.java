package kz.edu.mobileoperator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания и обновления тарифного плана.
 */
@Getter
@Setter
public class TariffRequestDto {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Positive
    private BigDecimal monthlyFee;

    private Integer includedMinutes;
    private Integer includedSms;
    private Integer includedGb;
}


