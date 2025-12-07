package kz.edu.mobileoperator.mapper;

import kz.edu.mobileoperator.dto.TariffRequestDto;
import kz.edu.mobileoperator.dto.TariffResponseDto;
import kz.edu.mobileoperator.model.Tariff;
import org.springframework.stereotype.Component;

/**
 * Маппер между сущностью {@link Tariff} и DTO.
 */
@Component
public class TariffMapper {

    public Tariff toEntity(TariffRequestDto dto) {
        Tariff tariff = new Tariff();
        tariff.setCode(dto.getCode());
        tariff.setName(dto.getName());
        tariff.setDescription(dto.getDescription());
        tariff.setMonthlyFee(dto.getMonthlyFee());
        tariff.setIncludedMinutes(dto.getIncludedMinutes());
        tariff.setIncludedSms(dto.getIncludedSms());
        tariff.setIncludedGb(dto.getIncludedGb());
        return tariff;
    }

    public void updateEntity(Tariff tariff, TariffRequestDto dto) {
        tariff.setName(dto.getName());
        tariff.setDescription(dto.getDescription());
        tariff.setMonthlyFee(dto.getMonthlyFee());
        tariff.setIncludedMinutes(dto.getIncludedMinutes());
        tariff.setIncludedSms(dto.getIncludedSms());
        tariff.setIncludedGb(dto.getIncludedGb());
    }

    public TariffResponseDto toDto(Tariff tariff) {
        TariffResponseDto dto = new TariffResponseDto();
        dto.setId(tariff.getId());
        dto.setCode(tariff.getCode());
        dto.setName(tariff.getName());
        dto.setDescription(tariff.getDescription());
        dto.setMonthlyFee(tariff.getMonthlyFee());
        dto.setIncludedMinutes(tariff.getIncludedMinutes());
        dto.setIncludedSms(tariff.getIncludedSms());
        dto.setIncludedGb(tariff.getIncludedGb());
        dto.setStatus(tariff.getStatus().name());
        return dto;
    }
}


