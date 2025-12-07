package kz.edu.mobileoperator.service;

import java.util.List;
import kz.edu.mobileoperator.model.Tariff;

/**
 * Сервисный слой для работы с тарифами.
 */
public interface TariffService {

    Tariff create(Tariff tariff);

    Tariff update(Long id, Tariff updatedTariff);

    Tariff getById(Long id);

    List<Tariff> getAll();

    Tariff archive(Long id);

    /**
     * Получить только активные тарифы.
     */
    List<Tariff> getActiveTariffs();
}




