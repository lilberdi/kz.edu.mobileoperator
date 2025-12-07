package kz.edu.mobileoperator.service;

import java.util.List;
import kz.edu.mobileoperator.dto.RecentActivityDto;

/**
 * Сервис для записи и чтения событий недавней активности.
 */
public interface RecentActivityService {

    /**
     * Зафиксировать новое событие.
     */
    void record(String type, String message, String entityType, Long entityId);

    /**
     * Получить последние N событий (по убыванию времени).
     */
    List<RecentActivityDto> getRecent(int limit);
}


