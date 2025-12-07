package kz.edu.mobileoperator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания и обновления клиента.
 */
@Getter
@Setter
public class CustomerRequestDto {

    @NotBlank
    @Size(max = 200)
    private String fullName;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 50)
    private String documentNumber;
}


