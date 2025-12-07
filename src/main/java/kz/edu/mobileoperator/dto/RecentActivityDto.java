package kz.edu.mobileoperator.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO для передачи информации о недавней активности в админ-панель.
 */
@Getter
@AllArgsConstructor
public class RecentActivityDto {

    private Long id;
    private OffsetDateTime timestamp;
    private String type;
    private String message;
    private String entityType;
    private Long entityId;
}


