package kz.edu.mobileoperator.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import kz.edu.mobileoperator.dto.CustomerRequestDto;
import kz.edu.mobileoperator.dto.CustomerResponseDto;
import kz.edu.mobileoperator.dto.StatusUpdateRequest;
import kz.edu.mobileoperator.exception.BusinessException;
import kz.edu.mobileoperator.mapper.CustomerMapper;
import kz.edu.mobileoperator.model.Customer;
import kz.edu.mobileoperator.model.CustomerStatus;
import kz.edu.mobileoperator.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для операций с клиентами.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    /**
     * Создать нового клиента.
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto requestDto) {
        Customer customer = customerMapper.toEntity(requestDto);
        Customer created = customerService.create(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(created);
        return ResponseEntity
                .created(URI.create("/api/customers/" + created.getId()))
                .body(responseDto);
    }

    /**
     * Получить список клиентов. При наличии параметра q выполняется поиск.
     */
    @GetMapping
    public List<CustomerResponseDto> getCustomers(@RequestParam(value = "q", required = false) String query) {
        List<Customer> customers = (query == null || query.isBlank())
                ? customerService.getAll()
                : customerService.search(query);
        return customers.stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить клиента по идентификатору.
     */
    @GetMapping("/{id}")
    public CustomerResponseDto getCustomer(@PathVariable Long id) {
        return customerMapper.toDto(customerService.getById(id));
    }

    /**
     * Обновить данные клиента.
     */
    @PutMapping("/{id}")
    public CustomerResponseDto updateCustomer(@PathVariable Long id,
                                              @Valid @RequestBody CustomerRequestDto requestDto) {
        Customer updatedData = customerMapper.toEntity(requestDto);
        Customer updated = customerService.update(id, updatedData);
        return customerMapper.toDto(updated);
    }

    /**
     * Заблокировать клиента.
     */
    @PostMapping("/{id}/block")
    public CustomerResponseDto blockCustomer(@PathVariable Long id) {
        return customerMapper.toDto(customerService.block(id));
    }

    /**
     * Разблокировать клиента.
     */
    @PostMapping("/{id}/unblock")
    public CustomerResponseDto unblockCustomer(@PathVariable Long id) {
        return customerMapper.toDto(customerService.unblock(id));
    }

    /**
     * Универсальное изменение статуса клиента (ACTIVE/BLOCKED).
     */
    @PatchMapping("/{id}/status")
    public CustomerResponseDto updateCustomerStatus(@PathVariable Long id,
                                                    @Valid @RequestBody StatusUpdateRequest request) {
        try {
            CustomerStatus status = CustomerStatus.valueOf(request.getStatus());
            return customerMapper.toDto(customerService.updateStatus(id, status));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid customer status: " + request.getStatus());
        }
    }

    /**
     * Удалить клиента.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteById(id);
    }
}




