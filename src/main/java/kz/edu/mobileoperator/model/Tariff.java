package kz.edu.mobileoperator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Тариф мобильного оператора.
 */
@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный код тарифа (для внутренних систем).
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "monthly_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyFee;

    @Column(name = "included_minutes")
    private Integer includedMinutes;

    @Column(name = "included_sms")
    private Integer includedSms;

    @Column(name = "included_gb")
    private Integer includedGb;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TariffStatus status = TariffStatus.ACTIVE;

    @OneToMany(mappedBy = "tariff")
    private List<Subscription> subscriptions = new ArrayList<>();

    public Tariff(String code,
                  String name,
                  String description,
                  BigDecimal monthlyFee,
                  Integer includedMinutes,
                  Integer includedSms,
                  Integer includedGb) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.monthlyFee = monthlyFee;
        this.includedMinutes = includedMinutes;
        this.includedSms = includedSms;
        this.includedGb = includedGb;
        this.status = TariffStatus.ACTIVE;
    }
}


