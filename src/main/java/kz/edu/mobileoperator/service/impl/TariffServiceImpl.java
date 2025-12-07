package kz.edu.mobileoperator.service.impl;

import java.util.List;
import kz.edu.mobileoperator.exception.BusinessException;
import kz.edu.mobileoperator.exception.ResourceNotFoundException;
import kz.edu.mobileoperator.model.Tariff;
import kz.edu.mobileoperator.model.TariffStatus;
import kz.edu.mobileoperator.repository.TariffRepository;
import kz.edu.mobileoperator.service.RecentActivityService;
import kz.edu.mobileoperator.service.TariffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с тарифами.
 */
@Service
@Transactional
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final RecentActivityService recentActivityService;

    public TariffServiceImpl(TariffRepository tariffRepository,
                             RecentActivityService recentActivityService) {
        this.tariffRepository = tariffRepository;
        this.recentActivityService = recentActivityService;
    }

    @Override
    public Tariff create(Tariff tariff) {
        tariff.setId(null);
        if (tariffRepository.existsByCode(tariff.getCode())) {
            throw new BusinessException("Tariff with code '" + tariff.getCode() + "' already exists");
        }
        tariff.setStatus(TariffStatus.ACTIVE);
        Tariff saved = tariffRepository.save(tariff);
        // Логируем создание нового тарифа
        recentActivityService.record(
                "TARIFF_CREATED",
                "Tariff created: " + saved.getName(),
                "TARIFF",
                saved.getId()
        );
        return saved;
    }

    @Override
    public Tariff update(Long id, Tariff updatedTariff) {
        Tariff existing = getById(id);
        existing.setName(updatedTariff.getName());
        existing.setDescription(updatedTariff.getDescription());
        existing.setMonthlyFee(updatedTariff.getMonthlyFee());
        existing.setIncludedMinutes(updatedTariff.getIncludedMinutes());
        existing.setIncludedSms(updatedTariff.getIncludedSms());
        existing.setIncludedGb(updatedTariff.getIncludedGb());
        Tariff saved = tariffRepository.save(existing);
        recentActivityService.record(
                "TARIFF_UPDATED",
                "Tariff updated: " + saved.getName(),
                "TARIFF",
                saved.getId()
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Tariff getById(Long id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tariff> getAll() {
        return tariffRepository.findAll();
    }

    @Override
    public Tariff archive(Long id) {
        Tariff tariff = getById(id);
        tariff.setStatus(TariffStatus.ARCHIVED);
        Tariff saved = tariffRepository.save(tariff);
        recentActivityService.record(
                "TARIFF_STATUS_CHANGED",
                "Tariff archived: " + saved.getName(),
                "TARIFF",
                saved.getId()
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tariff> getActiveTariffs() {
        return tariffRepository.findByStatus(TariffStatus.ACTIVE);
    }

    @Override
    public Tariff updateStatus(Long id, TariffStatus status) {
        Tariff tariff = getById(id);
        tariff.setStatus(status);
        Tariff saved = tariffRepository.save(tariff);
        recentActivityService.record(
                "TARIFF_STATUS_CHANGED",
                "Tariff status set to " + status + ": " + saved.getName(),
                "TARIFF",
                saved.getId()
        );
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        Tariff tariff = getById(id);
        tariffRepository.delete(tariff);
        recentActivityService.record(
                "TARIFF_DELETED",
                "Tariff deleted: " + tariff.getName(),
                "TARIFF",
                tariff.getId()
        );
    }
}


