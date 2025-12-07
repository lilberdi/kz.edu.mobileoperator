package kz.edu.mobileoperator.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import kz.edu.mobileoperator.dto.TariffRequestDto;
import kz.edu.mobileoperator.dto.TariffResponseDto;
import kz.edu.mobileoperator.mapper.TariffMapper;
import kz.edu.mobileoperator.model.Tariff;
import kz.edu.mobileoperator.service.TariffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для операций с тарифами.
 */
@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    private final TariffService tariffService;
    private final TariffMapper tariffMapper;

    public TariffController(TariffService tariffService, TariffMapper tariffMapper) {
        this.tariffService = tariffService;
        this.tariffMapper = tariffMapper;
    }

    /**
     * Создать новый тариф.
     */
    @PostMapping
    public ResponseEntity<TariffResponseDto> createTariff(@Valid @RequestBody TariffRequestDto requestDto) {
        Tariff tariff = tariffMapper.toEntity(requestDto);
        Tariff created = tariffService.create(tariff);
        TariffResponseDto responseDto = tariffMapper.toDto(created);
        return ResponseEntity
                .created(URI.create("/api/tariffs/" + created.getId()))
                .body(responseDto);
    }

    /**
     * Получить список тарифов.
     * Параметр activeOnly=true возвращает только активные тарифы.
     */
    @GetMapping
    public List<TariffResponseDto> getTariffs(
            @RequestParam(value = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) {
        List<Tariff> tariffs = activeOnly ? tariffService.getActiveTariffs() : tariffService.getAll();
        return tariffs.stream()
                .map(tariffMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить тариф по идентификатору.
     */
    @GetMapping("/{id}")
    public TariffResponseDto getTariff(@PathVariable Long id) {
        return tariffMapper.toDto(tariffService.getById(id));
    }

    /**
     * Обновить параметры тарифа.
     */
    @PutMapping("/{id}")
    public TariffResponseDto updateTariff(@PathVariable Long id,
                                          @Valid @RequestBody TariffRequestDto requestDto) {
        Tariff existing = tariffService.getById(id);
        tariffMapper.updateEntity(existing, requestDto);
        Tariff updated = tariffService.update(id, existing);
        return tariffMapper.toDto(updated);
    }

    /**
     * Архивировать тариф.
     */
    @PostMapping("/{id}/archive")
    public TariffResponseDto archiveTariff(@PathVariable Long id) {
        return tariffMapper.toDto(tariffService.archive(id));
    }
}




