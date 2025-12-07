package kz.edu.mobileoperator.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kz.edu.mobileoperator.dto.RecentActivityDto;
import kz.edu.mobileoperator.model.RecentActivity;
import kz.edu.mobileoperator.repository.RecentActivityRepository;
import kz.edu.mobileoperator.service.RecentActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с журналом недавней активности.
 */
@Service
@Transactional
public class RecentActivityServiceImpl implements RecentActivityService {

    private final RecentActivityRepository recentActivityRepository;

    public RecentActivityServiceImpl(RecentActivityRepository recentActivityRepository) {
        this.recentActivityRepository = recentActivityRepository;
    }

    @Override
    public void record(String type, String message, String entityType, Long entityId) {
        RecentActivity activity = new RecentActivity(
                OffsetDateTime.now(),
                type,
                message,
                entityType,
                entityId
        );
        recentActivityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentActivityDto> getRecent(int limit) {
        // репозиторий уже ограничивает выборку 10-ю записями, но здесь оставляем возможность
        // в будущем расширить до произвольного лимита.
        return recentActivityRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(a -> new RecentActivityDto(
                        a.getId(),
                        a.getCreatedAt(),
                        a.getType(),
                        a.getMessage(),
                        a.getEntityType(),
                        a.getEntityId()
                ))
                .collect(Collectors.toList());
    }
}


