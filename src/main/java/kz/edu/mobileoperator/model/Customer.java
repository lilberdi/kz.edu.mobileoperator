package kz.edu.mobileoperator.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность клиента мобильного оператора.
 * <p>
 * Клиент может иметь несколько подписок на разные тарифы.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "document_number", nullable = false, length = 50)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

    /**
     * Удобный конструктор для создания нового клиента из сервисного слоя.
     */
    public Customer(String fullName, String phoneNumber, String documentNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.documentNumber = documentNumber;
        this.status = CustomerStatus.ACTIVE;
    }

    /**
     * Добавить подписку к клиенту и установить двустороннюю связь.
     */
    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.setCustomer(this);
    }

    /**
     * Удалить подписку у клиента.
     */
    public void removeSubscription(Subscription subscription) {
        subscriptions.remove(subscription);
        subscription.setCustomer(null);
    }
}


