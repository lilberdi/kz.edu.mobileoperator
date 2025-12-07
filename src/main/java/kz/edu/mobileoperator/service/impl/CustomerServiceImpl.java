package kz.edu.mobileoperator.service.impl;

import java.util.List;
import kz.edu.mobileoperator.exception.ResourceNotFoundException;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.CustomerStatus;
import kz.edu.mobileoperator.repository.CustomerRepository;
import kz.edu.mobileoperator.service.CustomerService;
import kz.edu.mobileoperator.service.RecentActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с клиентами.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final RecentActivityService recentActivityService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               RecentActivityService recentActivityService) {
        this.customerRepository = customerRepository;
        this.recentActivityService = recentActivityService;
    }

    @Override
    public Customer create(Customer customer) {
        customer.setId(null);
        customer.setStatus(CustomerStatus.ACTIVE);
        Customer saved = customerRepository.save(customer);
        // Логируем создание нового клиента для блока "Recent Activity"
        recentActivityService.record(
                "CUSTOMER_CREATED",
                "Customer created: " + saved.getFullName(),
                "CUSTOMER",
                saved.getId()
        );
        return saved;
    }

    @Override
    public Customer update(Long id, Customer updatedCustomer) {
        Customer existing = getById(id);
        existing.setFullName(updatedCustomer.getFullName());
        existing.setPhoneNumber(updatedCustomer.getPhoneNumber());
        existing.setDocumentNumber(updatedCustomer.getDocumentNumber());
        Customer saved = customerRepository.save(existing);
        recentActivityService.record(
                "CUSTOMER_UPDATED",
                "Customer updated: " + saved.getFullName(),
                "CUSTOMER",
                saved.getId()
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer block(Long id) {
        Customer customer = getById(id);
        customer.setStatus(CustomerStatus.BLOCKED);
        Customer saved = customerRepository.save(customer);
        recentActivityService.record(
                "CUSTOMER_STATUS_CHANGED",
                "Customer blocked: " + saved.getFullName(),
                "CUSTOMER",
                saved.getId()
        );
        return saved;
    }

    @Override
    public Customer unblock(Long id) {
        Customer customer = getById(id);
        customer.setStatus(CustomerStatus.ACTIVE);
        Customer saved = customerRepository.save(customer);
        recentActivityService.record(
                "CUSTOMER_STATUS_CHANGED",
                "Customer unblocked: " + saved.getFullName(),
                "CUSTOMER",
                saved.getId()
        );
        return saved;
    }

    @Override
    public Customer updateStatus(Long id, CustomerStatus status) {
        Customer customer = getById(id);
        customer.setStatus(status);
        Customer saved = customerRepository.save(customer);
        recentActivityService.record(
                "CUSTOMER_STATUS_CHANGED",
                "Customer status set to " + status + ": " + saved.getFullName(),
                "CUSTOMER",
                saved.getId()
        );
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        Customer customer = getById(id);
        customerRepository.delete(customer);
        recentActivityService.record(
                "CUSTOMER_DELETED",
                "Customer deleted: " + customer.getFullName(),
                "CUSTOMER",
                customer.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> search(String query) {
        if (query == null || query.isBlank()) {
            return customerRepository.findAll();
        }
        // Примитивный вариант поиска: сначала по ФИО, затем по номеру телефона.
        List<Customer> byName = customerRepository.findByFullNameContainingIgnoreCase(query);
        if (!byName.isEmpty()) {
            return byName;
        }
        return customerRepository.findByPhoneNumber(query)
                .map(List::of)
                .orElseGet(List::of);
    }
}


