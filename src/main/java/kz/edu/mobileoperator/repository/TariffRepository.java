package kz.edu.mobileoperator.repository;

import java.util.List;
import kz.edu.mobileoperator.model.Tariff;
import kz.edu.mobileoperator.model.TariffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link Tariff}.
 */
@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    List<Tariff> findByStatus(TariffStatus status);

    long countByStatus(TariffStatus status);

    boolean existsByCode(String code);
}


