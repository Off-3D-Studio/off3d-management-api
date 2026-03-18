package com.off3d.studio.sales.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.infra.exceptions.BusinessException;
import com.off3d.studio.infra.exceptions.ResourceNotFoundException;
import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.dto.CustomerRequestDTO;
import com.off3d.studio.sales.dto.CustomerResponseDTO;
import com.off3d.studio.sales.dto.OrderResponseDTO;
import com.off3d.studio.sales.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.off3d.studio.infra.config.ErrorMessages.CUSTOMER_NOT_FOUND;

@Slf4j
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponseDTO save(CustomerRequestDTO dto) {
        User currentUser = getCurrentUser();
        log.info("Sócio [{}] - ID: {} cadastrando novo cliente: {}",
                currentUser.getName(), currentUser.getId(), dto.name());

        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
        customer.setCreatedBy(currentUser);

        return mapToResponseDTO(customerRepository.save(customer), false);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(customer -> mapToResponseDTO(customer, false))
                .toList();
    }

    public Customer findById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND + id));
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO findByIdDetailed(UUID id) {
        return mapToResponseDTO(findById(id), true);
    }

    @Transactional
    public CustomerResponseDTO update(UUID id, CustomerRequestDTO dto) {
        log.info("Atualizando dados do cliente ID: {}", id);

        Customer customer = findById(id);
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());

        return mapToResponseDTO(customerRepository.save(customer), true);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do cliente ID: {}", id);

        Customer customer = findById(id);

        try {
            customerRepository.delete(customer);
            log.info("Cliente {} removido com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Bloqueio: Cliente {} possui pedidos vinculados e não pode ser deletado", id);
            throw new BusinessException("Não é possível excluir um cliente que possui pedidos vinculados.");
        }
    }

    @Transactional
    public CustomerResponseDTO upsert(CustomerRequestDTO dto) {
        log.info("Operação de Upsert para e-mail: {}", dto.email());

        Customer customer = customerRepository.findByEmail(dto.email())
                .map(existingCustomer -> {
                    log.info("Cliente encontrado. Atualizando ID: {}", existingCustomer.getId());
                    existingCustomer.setName(dto.name());
                    existingCustomer.setPhone(dto.phone());
                    return existingCustomer;
                })
                .orElseGet(() -> {
                    log.info("Cliente não encontrado. Criando novo.");
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(dto.email());
                    newCustomer.setName(dto.name());
                    newCustomer.setPhone(dto.phone());
                    newCustomer.setCreatedBy(getCurrentUser());
                    return newCustomer;
                });

        return mapToResponseDTO(customerRepository.save(customer), false);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private CustomerResponseDTO mapToResponseDTO(Customer customer, boolean detailed) {
        Set<OrderResponseDTO> orderDTOs = Set.of();

        if (detailed && customer.getOrders() != null) {
            orderDTOs = customer.getOrders().stream()
                    .map(order -> new OrderResponseDTO(
                            order.getId(),
                            order.getOrderDate(),
                            order.getTotalPrice(),
                            order.getStatus()
                    ))
                    .collect(Collectors.toUnmodifiableSet());
        }

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                orderDTOs
        );
    }
}