package kz.edu.mobileoperator.repository;

import java.util.List;
import java.util.Optional;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link Customer}.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    List<Customer> findByFullNameContainingIgnoreCase(String fullName);

    List<Customer> findByStatus(CustomerStatus status);
}


