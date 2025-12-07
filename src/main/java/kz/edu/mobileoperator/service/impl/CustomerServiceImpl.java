package kz.edu.mobileoperator.service.impl;

import java.util.List;
import kz.edu.mobileoperator.exception.ResourceNotFoundException;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.CustomerStatus;
import kz.edu.mobileoperator.repository.CustomerRepository;
import kz.edu.mobileoperator.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с клиентами.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer create(Customer customer) {
        customer.setId(null);
        customer.setStatus(CustomerStatus.ACTIVE);
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Long id, Customer updatedCustomer) {
        Customer existing = getById(id);
        existing.setFullName(updatedCustomer.getFullName());
        existing.setPhoneNumber(updatedCustomer.getPhoneNumber());
        existing.setDocumentNumber(updatedCustomer.getDocumentNumber());
        return customerRepository.save(existing);
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
        return customerRepository.save(customer);
    }

    @Override
    public Customer unblock(Long id) {
        Customer customer = getById(id);
        customer.setStatus(CustomerStatus.ACTIVE);
        return customerRepository.save(customer);
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


