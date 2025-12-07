package kz.edu.mobileoperator.service;

import java.util.List;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.CustomerStatus;

/**
 * Сервисный слой для работы с клиентами.
 * <p>
 * Описывает бизнес-операции над сущностью {@link Customer}.
 */
public interface CustomerService {

    Customer create(Customer customer);

    Customer update(Long id, Customer updatedCustomer);

    Customer getById(Long id);

    List<Customer> getAll();

    Customer block(Long id);

    Customer unblock(Long id);

    /**
     * Изменить статус клиента (ACTIVE/BLOCKED).
     */
    Customer updateStatus(Long id, CustomerStatus status);

    /**
     * Удалить клиента по идентификатору.
     */
    void deleteById(Long id);

    /**
     * Простой вариант поиска клиентов по части ФИО или номеру телефона.
     *
     * @param query часть имени или номера телефона
     * @return список найденных клиентов
     */
    List<Customer> search(String query);
}




