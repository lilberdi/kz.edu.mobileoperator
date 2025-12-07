package kz.edu.mobileoperator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Простейшая сущность для хранения последних действий в системе.
 * Используется для блока "Recent Activity" на дашборде админ-панели.
 */
@Entity
@Table(name = "recent_activity")
@Getter
@Setter
@NoArgsConstructor
public class RecentActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Время события (UTC).
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Краткий тип события (например CUSTOMER_CREATED, TARIFF_ARCHIVED и т.п.).
     */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /**
     * Человеко-читаемое описание действия.
     */
    @Column(name = "message", nullable = false, length = 255)
    private String message;

    /**
     * Тип сущности (CUSTOMER, TARIFF, SUBSCRIPTION).
     */
    @Column(name = "entity_type", length = 30)
    private String entityType;

    /**
     * Идентификатор сущности, к которой относится событие.
     */
    @Column(name = "entity_id")
    private Long entityId;

    public RecentActivity(OffsetDateTime createdAt,
                          String type,
                          String message,
                          String entityType,
                          Long entityId) {
        this.createdAt = createdAt;
        this.type = type;
        this.message = message;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}


