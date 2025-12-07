package kz.edu.mobileoperator.repository;

import java.util.List;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.Subscription;
import kz.edu.mobileoperator.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link Subscription}.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByCustomerAndStatus(Customer customer, SubscriptionStatus status);

    List<Subscription> findByStatus(SubscriptionStatus status);

    long countByStatus(SubscriptionStatus status);
}


